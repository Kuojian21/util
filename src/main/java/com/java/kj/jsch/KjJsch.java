package com.java.kj.jsch;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import com.google.common.base.Strings;
import com.java.kj.base.KjPool;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class KjJsch extends KjPool<ChannelSftp, Boolean> {

	private GenericObjectPool<ChannelSftp> pool;

	private final Action<ChannelSftp, Boolean> UPLOAD = new Action<ChannelSftp, Boolean>() {
		@Override
		public Boolean action(ChannelSftp sftp, Object... objs) throws Exception {
			String directory = (String) objs[0];
			String file = (String) objs[1];
			InputStream is = (InputStream) objs[2];
			String root = null;
			try {
				root = sftp.pwd();
				if (!Strings.isNullOrEmpty(directory)) {
					String[] dirs = directory.split("/");
					for (String dir : dirs) {
						if (!Strings.isNullOrEmpty(dir)) {
							try {
								sftp.cd(dir);
							} catch (SftpException sException) {
								if (ChannelSftp.SSH_FX_NO_SUCH_FILE == sException.id) {
									sftp.mkdir(dir);
									sftp.cd(dir);
								} else {
									return false;
								}
							}
						}
					}
				}
				sftp.put(is, file);
				return true;
			} finally {
				if (Strings.isNullOrEmpty(root)) {
					try {
						sftp.cd(root);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (is != null) {
					try {
						is.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	};

	private final Action<ChannelSftp, Boolean> DOWNLOAD = new Action<ChannelSftp, Boolean>() {
		@Override
		public Boolean action(ChannelSftp sftp, Object... objs) throws Exception {
			String directory = (String) objs[0];
			String file = (String) objs[1];
			OutputStream os = (OutputStream) objs[2];
			String root = null;
			try {
				root = sftp.pwd();
				if (!Strings.isNullOrEmpty(directory)) {
					sftp.cd(directory);
				}
				sftp.get(file, os);
				os.flush();
				return true;
			} finally {
				if (os != null) {
					try {
						os.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (Strings.isNullOrEmpty(root)) {
					try {
						sftp.cd(root);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	};

	public KjJsch(String host, int port, String username, String password) {
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

	public KjJsch(String host, int port, String username, String prvfile, String pubfile, byte[] passphrase) {
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

	public boolean upload(String directory, String file, InputStream is) {
		try {
			return this.execute(UPLOAD, directory, file, is);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean download(String directory, String file, OutputStream os) {
		try {
			return this.execute(DOWNLOAD, directory, file, os);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public ChannelSftp borrowObject() throws Exception {
		return pool.borrowObject();
	}

	@Override
	public void returnObject(ChannelSftp obj) throws Exception {
		if (obj != null) {
			pool.returnObject(obj);
		}
	}

	@Override
	public void close() throws Exception {
		pool.close();
	}

}
