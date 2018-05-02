package com.jsch;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Vector;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import com.google.common.base.Strings;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.SftpException;
import com.tools.io.StreamTool;
import com.tools.logger.LogConstant;

public class Jsch {

	private GenericObjectPool<ChannelSftp> pool;

	public Jsch(String host, int port, String username, String password) {
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMinIdle(10);
		config.setMaxTotal(100);
		config.setMaxWaitMillis(30000);
		pool = new GenericObjectPool<ChannelSftp>(new BasePooledObjectFactory<ChannelSftp>() {
			@Override
			public PooledObject<ChannelSftp> wrap(ChannelSftp sftp) {
				return new DefaultPooledObject<ChannelSftp>(sftp) {
					@Override
					public void invalidate() {
						super.invalidate();
						try {
							sftp.disconnect();
							sftp.getSession().disconnect();
						} catch (JSchException e) {
							e.printStackTrace();
						}
					}
				};
			}

			@Override
			public ChannelSftp create() throws Exception {
				JSch jsch = new JSch();
				Session sshSession = jsch.getSession(username, host, port);
				sshSession.setPassword(password);
				Properties sshConfig = new Properties();
				sshConfig.put("StrictHostKeyChecking", "no");
				sshSession.setConfig(sshConfig);
				sshSession.connect();
				Channel channel = sshSession.openChannel("sftp");
				channel.connect();
				return (ChannelSftp) channel;
			}

		}, config);
	}

	public Jsch(String host, int port, String username, String prvfile, String pubfile, byte[] passphrase) {
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMinIdle(10);
		config.setMaxTotal(100);
		config.setMaxWaitMillis(30000);
		pool = new GenericObjectPool<ChannelSftp>(new BasePooledObjectFactory<ChannelSftp>() {
			@Override
			public PooledObject<ChannelSftp> wrap(ChannelSftp sftp) {
				return new DefaultPooledObject<ChannelSftp>(sftp) {
					@Override
					public void invalidate() {
						super.invalidate();
						try {
							sftp.disconnect();
							sftp.getSession().disconnect();
						} catch (JSchException e) {
							e.printStackTrace();
						}
					}
				};
			}

			@Override
			public ChannelSftp create() throws Exception {
				JSch jsch = new JSch();
				jsch.addIdentity(prvfile, pubfile, passphrase);
				Session sshSession = jsch.getSession(username, host, port);
				Properties sshConfig = new Properties();
				sshConfig.put("StrictHostKeyChecking", "no");
				sshSession.setConfig(sshConfig);
				sshSession.connect();
				Channel channel = sshSession.openChannel("sftp");
				channel.connect();
				return (ChannelSftp) channel;
			}

		}, config);
	}

	public void upload(String directory, String file, InputStream is) {
		ChannelSftp sftp = null;
		String root = null;
		try {
			sftp = pool.borrowObject();
			root = sftp.pwd();
			if(Strings.isNullOrEmpty(directory)) {
				if(sftp.stat(directory) == null) {
					sftp.mkdir(directory);
				}
				sftp.cd(directory);
			}
			sftp.put(is, file);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (Strings.isNullOrEmpty(root)) {
				try {
					sftp.cd(root);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (sftp != null) {
				if (is != null) {
					try {
						is.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				pool.returnObject(sftp);
			}
		}
	}

	public void download(String directory, String file, OutputStream os) {
		ChannelSftp sftp = null;
		String root = null;
		try {
			sftp = pool.borrowObject();
			root = sftp.pwd();
			Jsch.changeDir(sftp, directory);
			BufferedOutputStream bos = StreamTool.buffer(os);
			sftp.get(file, bos);
			bos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (Strings.isNullOrEmpty(root)) {
				try {
					sftp.cd(root);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (sftp != null) {
				if (os != null) {
					try {
						os.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				pool.returnObject(sftp);
			}
		}
	}

	public static void changeDir(ChannelSftp sftp, String directory) throws SftpException {
		String module = "修改目录";
		if (!Strings.isNullOrEmpty(directory)) {
			String[] dirs = directory.split("/");
			for (String dir : dirs) {
				if (Strings.isNullOrEmpty(dir)) {
					continue;
				}
				Vector<?> vector = sftp.ls(".");
				boolean exist = false;
				for (Object item : vector) {
					LsEntry entry = (LsEntry) item;
					if (dir.equals(entry.getFilename())) {
						exist = true;
						break;
					}
				}
				if (!exist) {
					sftp.mkdir(dir);
				}
				sftp.cd(dir);
			}
		}
		LogConstant.runLog.info(module, "结束", "dir=" + sftp.pwd());
	}

}
