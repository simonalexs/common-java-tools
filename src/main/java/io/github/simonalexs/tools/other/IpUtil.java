package io.github.simonalexs.tools.other;
 
import java.lang.management.ManagementFactory;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Set;
 
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.Query;
 

public class IpUtil {
 
	/**
	 * 获取本机IP地址
	 * @return 1
	 * @throws SocketException 2
	 */
	public static String getIpAddress() throws SocketException {
	        String ipString = null;
	        Inet4Address inet4Address  = getInet4Address();
			if(inet4Address != null){
				/*NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inet4Address);
	    		for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
	    			ipString = address.getAddress().getHostAddress();
	    		}*/
				ipString = inet4Address.getHostAddress();
			}
	    return ipString;
	  }
	
	/**
	 * 获取tomcat容器的http端口
	 * @return 1
	 * @throws MalformedObjectNameException  2
	 */
	public static String getTomcatHttpPort() throws MalformedObjectNameException{
		MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
		Set<ObjectName> objectNames = beanServer.queryNames(new ObjectName("*:type=Connector,*"),
                Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
        String port = objectNames.iterator().next().getKeyProperty("port");
        return port;
	}
	
	/**
	 * 获取网络前缀长度，
	 * 如果长度为8，则表示掩码是255.0.0.0，
	 * 如果长度为16，则表示掩码是255.255.0.0，
	 * 如果长度为24，则表示掩码是255.255.255.0，
	 * @return 1
	 * @throws UnknownHostException  2
	 * @throws SocketException 3
	 */
	public static int getNetworkPrefixLength() throws UnknownHostException, SocketException{
		
		int networkPrefixLength = 0;
		Inet4Address inet4Address  = getInet4Address();
		if(inet4Address != null){
			NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inet4Address);
    		for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
    			if(address.getAddress() instanceof Inet4Address){
    				networkPrefixLength =  address.getNetworkPrefixLength();
    			}
    			
    		}
		}
		return networkPrefixLength;
	}
	
	/**
	 * 获取网络掩码255.0.0.0，255.0.0.0，255.0.0.0，
	 * @return 1
	 * @throws UnknownHostException  2
	 * @throws SocketException 3
	 */
	public static String getSubnet() throws UnknownHostException, SocketException{
		String subnet = null;
		int prefix = getNetworkPrefixLength();
		if(prefix > 0){
			if(prefix == 8){
				subnet = "255.0.0.0";
			}else if(prefix == 16){
				subnet = "255.255.0.0";
			}else if(prefix == 24){
				subnet = "255.255.255.0";
			}else if(prefix == 32){
				subnet = "255.255.255.255";
			}
		}
		return subnet;
	}
	
	private static Inet4Address getInet4Address() throws SocketException{
		 Inet4Address inet4Address = null;
		 Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
	      InetAddress ip = null;
	      while (allNetInterfaces.hasMoreElements()) {
	        NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
	        //用于排除回送接口,非虚拟网卡,未在使用中的网络接口.
	        if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
	          continue;
	        } else {
	          Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
	          while (addresses.hasMoreElements()) {
	            ip = addresses.nextElement();
	            if (ip != null && ip instanceof Inet4Address) {
	            	inet4Address = (Inet4Address)ip;
	                break;
	            }
	          }
	          if(inet4Address != null){
	        	  break;
	          }
	        }
	      }
	      return inet4Address;
	}
	
	public static void main(String[] args) throws SocketException, UnknownHostException {
		System.out.println(getIpAddress());
	   System.out.println(getNetworkPrefixLength());
	   System.out.println(getSubnet());
	}
}