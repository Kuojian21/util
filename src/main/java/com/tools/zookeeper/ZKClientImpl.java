package com.tools.zookeeper;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

/**
 * @author KevinLiao 2011-12-29
 */
public class ZKClientImpl implements Watcher, AsyncCallback.VoidCallback, ZKClient
{

	private static final Log logger = LogFactory.getLog(ZKClientImpl.class);
	/**
	 * server 地址
	 */
	private String server;
	/**
	 * 创建节点的目录（结尾不带"/"）
	 */
	private String rootDirectory;
	/**
	 * 创建临时节点的前缀
	 */
	private String znodePrefix;
	/**
	 * 选举执行节点的方式<br>
	 * 0 初始化时选举一次（默认）<br>
	 * 1 每次执行任务皆重新选举
	 */
	private int electionType = 0;
	/**
	 * 连接zkserver超时时间
	 */
	private int timeout = 10000;
	/**
	 * 选取master的timeout
	 */
	private int justifyMasterTimeout = 10000;
	/**
	 * 是否使用acl，默认为0，表示不使用<br>
	 * 不使用则scheme和auth无意义
	 */
	private int useACL;
	/**
	 * 进行节点acl限制的scheme
	 */
	private String scheme = "digest";
	/**
	 * 进行节点acl限制的auth串
	 */
	private String auth = "admin:admin";

	/**
	 * 连接的实例
	 */
	private ZooKeeper zk;
	/**
	 * 创建的临时节点的全路径
	 */
	private String path;
	/**
	 * 是否是master
	 */
	private boolean isMaster;
	/**
	 * acl列表
	 */
	private List<ACL> acls = Ids.OPEN_ACL_UNSAFE;

	/**
	 * 通过这个方法取是否是master<br>
	 * 如果设置选取master方法是0（默认），且设置监听的path已经存在，则直接返回isMaster值<br>
	 * 否则需要wait指定时间（默认3s），待确认后再返回，如果没有取到自己设定的path则认为自己不是master，否则直接返回isMaster值<br>
	 * 
	 * @return
	 */
	public boolean isMaster()
	{

		// if (electionType == 0 && null != path)
		// {
		// return isMaster;
		// }
		synchronized (this)
		{
			try
			{
				// 等于重新初始化一次
				// init();
				doLeaderElection();
				// 等着吧，领导们要开会
				this.wait(justifyMasterTimeout);
			}
			catch (InterruptedException e)
			{
				logger.error(e.getMessage(), e);
				return false;
			}
			return null == path ? false : isMaster;
		}
	}

	/**
	 * 返回zk是否在正常连接，如果没有请应用自己判断如何处理
	 * 
	 * @return
	 */
	public boolean isAlive()
	{

		return zk == null ? false : zk.getState() == null ? false : zk.getState().isAlive();
	}

	@PostConstruct
	public void init()
	{

		// 先重置，确保isMaster方法取到的是新的决议
		path = null;
		isMaster = false;
		try
		{
			// 如果之前连着，要先断掉
			if (null != zk && zk.getState().isAlive())
			{
				zk.close();
			}
			connect();
			if (0 != useACL && acls == Ids.OPEN_ACL_UNSAFE)
			{
				Id authId = new Id("digest", DigestAuthenticationProvider.generateDigest(auth));
				Id anyId = new Id("world", "anyone");
				acls.clear();
				acls.add(new ACL(ZooDefs.Perms.ALL ^ ZooDefs.Perms.DELETE, anyId));
				acls.add(new ACL(ZooDefs.Perms.DELETE, authId));
			}
			// 如果没有rootDirectoty节点，则创建
			if (null == zk.exists(rootDirectory, false))
			{
				zk.create(rootDirectory, new byte[0], acls, CreateMode.PERSISTENT);
			}
			path = zk.create(rootDirectory + "/" + znodePrefix, getZnodeContent().getBytes(), acls,
					CreateMode.EPHEMERAL_SEQUENTIAL);
			// System.out.println("create path " + path);
			if (logger.isInfoEnabled())
			{
				logger.info(" sync success");
			}
			zk.sync(rootDirectory, this, null);
		}
		catch (IOException e)
		{
			logger.fatal(e.getMessage(), e);
		}
		catch (KeeperException e)
		{
			logger.fatal(e.getMessage(), e);
		}
		catch (InterruptedException e)
		{
			logger.fatal(e.getMessage(), e);
		}
		catch (NoSuchAlgorithmException e)
		{
			logger.fatal(e.getMessage(), e);
		}
	}

	@PreDestroy
	public void destroy()
	{
		if (zk != null)
		{
			try
			{
				zk.close();
			}
			catch (InterruptedException e)
			{
				logger.fatal(e.getMessage(), e);
			}
		}
	}

	private void connect() throws IOException
	{

		zk = new ZooKeeper(server, timeout, this);
		zk.addAuthInfo(scheme, auth.getBytes());
	}

	/**
	 * 这个方法的最大作用是notify在isMaster()方法中等待的线程
	 * 
	 * @param isMaster
	 */
	private void setMaster(boolean isMaster)
	{

		this.isMaster = isMaster;
		synchronized (this)
		{
			this.notifyAll();
		}
	}

	private String getZnodeContent()
	{
		try
		{
			return InetAddress.getLocalHost().getHostAddress();
		}
		catch (UnknownHostException e)
		{
			return "0";
		}
	}

	/**
	 * 处理sync方法的异步返回
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void processResult(int rc, String path, Object ctx)
	{

		switch (rc)
		{
		case Code.Ok:
			if (logger.isDebugEnabled())
			{
				logger.debug(" sync success");
			}
			doLeaderElection();
			break;
		default:
			break;
		}

	}

	@Override
	public void process(WatchedEvent event)
	{

		String eventPath = event.getPath();
		if (event.getType() == Event.EventType.None)
		{
			KeeperState state = event.getState();
			// 如果已经过期，要重新连接，加入下一次选举leader客户端
			if (KeeperState.Expired == state)
			{
				// setMaster(false);
				if (logger.isInfoEnabled())
				{
					logger.info("path " + path + " is expired");
				}
				// 没有path了，因为断掉了
				// path = null;
				// connect();
				init();
			}
		}
		else if (Watcher.Event.EventType.NodeDeleted == event.getType() || !eventPath.equals(rootDirectory))
		{
			if (logger.isDebugEnabled())
			{
				logger.debug(" find " + event.getPath() + " was deleted, it'm my turn!");
			}
			setMaster(true);
			doSth();
		}

	}

	/**
	 * 选取master
	 */
	private void doLeaderElection()
	{

		logger.info("zk = " + zk.toString());
		if (StringUtils.isBlank(path))
		{
			init();
			return;
		}
		try
		{
			List<String> znodeList = zk.getChildren(rootDirectory, false);
			Collections.sort(znodeList);
			int size = znodeList.size();
			boolean isPathExist = false;
			for (int i = 0; i < size; i++)
			{
				String s = znodeList.get(i);
				if (path.equals(rootDirectory + "/" + s))
				{
					isPathExist = true;
					if (i == 0)
					{
						setMaster(true);
						doSth();
					}
					else
					{
						// 从getChildren方法获取的列表中选择前一个自己的节点，设置watcher，这里要考虑获取时存在，但设置watcher时已经不存在的znode的情形
						int j = i;
						Stat stat = null;
						do
						{
							// 这里先不设置watcher
							stat = zk.exists(rootDirectory + "/" + znodeList.get(--j), false);
						}
						while (j >= 0 && null == stat);
						// 如果没有前一个节点，自己就是master（矬子拔将军？）
						if (null == stat)
						{
							setMaster(true);
							doSth();
						}
						else
						{
							// System.out.println("set watcher on " +
							// znodeList.get(j));
							stat = zk.exists(rootDirectory + "/" + znodeList.get(j), this);
							setMaster(false);
						}
					}
					// if this is the first node, there 's no need to watch
					// anything
					break;
				}
			}
			if (!isPathExist)
			{
				init();
				return;
			}
		}
		catch (KeeperException e)
		{
			logger.error(e.getMessage(), e);
		}
		catch (InterruptedException e)
		{
			logger.error(e.getMessage(), e);
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}

	}

	/**
	 * 要执行的业务方法，这是个空方法，但是可以用来做aop，由具体业务类在after后织入逻辑
	 */
	@Override
	public void doSth()
	{

		System.out.println("dosth");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

		// TODO Auto-generated method stub

	}

	// the follows are set methods
	public void setServer(String server)
	{

		this.server = server;
	}

	public void setRootDirectory(String rootDirectory)
	{

		this.rootDirectory = rootDirectory;
	}

	public void setZnodePrefix(String znodePrefix)
	{

		this.znodePrefix = znodePrefix;
	}

	public void setElectionType(int electionType)
	{

		this.electionType = electionType;
	}

	public void setTimeout(int timeout)
	{

		this.timeout = timeout;
	}

	public void setJustifyMasterTimeout(int justifyMasterTimeout)
	{

		this.justifyMasterTimeout = justifyMasterTimeout;
	}

	public void setScheme(String scheme)
	{

		this.scheme = scheme;
	}

	public void setAuth(String auth)
	{

		this.auth = auth;
	}

	public void setUseACL(int useACL)
	{

		this.useACL = useACL;
	}

}
