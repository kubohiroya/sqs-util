/*

 RemoteWindowDecorator.java

 Copyright 2004-2007 KUBO Hiroya (hiroya@cuc.ac.jp).

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 Created on 2006/01/09

 */
package net.sqs2.swing.process;

import java.awt.Toolkit;
import java.awt.Window;
import java.rmi.AlreadyBoundException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class RemoteWindowPopupDecorator {
	public static final String SINGLETON_SERVICE_NAME = "SingletonService";

	public static final String RMI_APP_SINGLETON_SERVICE_NAME = SINGLETON_SERVICE_NAME;
	 
	private static RemoteWindowAccessorImpl singleton = null;
	public ExecutorService executor = Executors.newSingleThreadExecutor();
	
	public static Window inactivate(int rmiPort) {
		return inactivate(rmiPort, SINGLETON_SERVICE_NAME);
	}
	
	public void shutdown(){
		executor.shutdown();
	}

	/**
	 * Usage: RemoteWindowDecorator.activate(port, window);
	 * 
	 * @param window
	 * @param rmiPort
	 */
	public boolean activate(Window window, int rmiPort) {
		try {
			if (singleton == null) {
				singleton = new RemoteWindowAccessorImpl(window);
				return activate(singleton, rmiPort, SINGLETON_SERVICE_NAME);
			}
		} catch (RemoteException ex) {
			throw new RuntimeException(ex);
		}
		return false;
	}

	public boolean activate(final Remote service, final int rmiPort, final String bindingName) {
		try{
			
			return executor.submit(new Callable<Boolean>(){
				public Boolean call(){
					Remote remote = null;
					try {
						final Registry registry = LocateRegistry.createRegistry(rmiPort);
						Logger.getLogger("RemoteWindowPopupDecorator").info("lookup...");
						remote = getRemoteService(registry, bindingName);
						Logger.getLogger("RemoteWindowPopupDecorator").info("...done.");
						if(remote == null){
							try{
								registry.bind(bindingName, service);
								return true;
							} catch (AlreadyBoundException ex) {
							}
						}
					} catch (RemoteException ex) {
					}
					Logger.getLogger("RemoteWindowPopupDecorator").info("Activate another instance.");
					activate(remote);
					return false;
				}

				private Remote getRemoteService(final Registry registry, final String bindingName) {
					try{
						Remote remote = executor.submit(new Callable<Remote>(){
							public Remote call(){
								try{
									return registry.lookup(bindingName);
								} catch(RemoteException ignore) {
									return null;
								} catch(NotBoundException ignore) {
									return null;
								}
							}
						}).get(1000, TimeUnit.MILLISECONDS);
						return remote;
					} catch (TimeoutException ex) {
					} catch (InterruptedException ex) {
					} catch (ExecutionException ex) {
					}
					return null;
				}
			}).get().booleanValue();

		}catch(ExecutionException ex){
		}catch(InterruptedException ex){
		}
		return false;
	}
	
	private static void activate(Remote remote) {
		RemoteWindowAccessor remoteService = (RemoteWindowAccessor) remote;
		Toolkit.getDefaultToolkit().beep();
		try {
			remoteService.toFront();
		} catch (RemoteException ignore) {
			ignore.printStackTrace();
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		Logger.getLogger("remote").info("RemoteWindow.toFront()");
		Logger.getLogger("remote").info("Exit.");
		// throw new RuntimeException("EXIT");
		System.exit(0);
	}

	public static Window inactivate(int rmiPort, String bindingName) {
		Window window = null;
		try {
			if (singleton != null) {
				window = singleton.getWindow();
				UnicastRemoteObject.unexportObject(singleton, true);
			}
			Registry registry = LocateRegistry.getRegistry(rmiPort);
			registry.unbind(bindingName);
		} catch (NotBoundException ignore) {
		} catch (RemoteException ignore) {
		}
		return window;
	}
}
