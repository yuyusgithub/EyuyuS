package db.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 彦祖 .
 */
public class FlowDataUtils {

    public static List<FlowData> get1000(){
        List<FlowData> list = new ArrayList<FlowData>();
        for (int i = 0 ;i<1000 ;i++){
            FlowData flowData = new FlowData();
            int r = IpUtils.getRandom(4);
            int srcIpnum = IpUtils.getRandomIpNum();
            String srcIp = IpUtils.num2ip(srcIpnum);
            int dstIpNum = IpUtils.getRandomIpNum();
            String dstIp = IpUtils.num2ip(dstIpNum);
            flowData.setLid(0);
            flowData.setProbe_id("00000000622211d301622288786f00ab");
            flowData.setProbe_ip("127.0.0.1");
            flowData.setInput(0);
            flowData.setOutput(0);
            flowData.setTimeFirst(System.currentTimeMillis()-1000);
            flowData.setTimeLast(System.currentTimeMillis());
            flowData.setSrcaddr(srcIp);
            flowData.setSrcaddrInt(srcIpnum);
            flowData.setDstaddr(dstIp);
            flowData.setDstaddrInt(dstIpNum);
            flowData.setSrcip(IpUtils.getRandom(100));
            flowData.setDstip(IpUtils.getRandom(100));
            flowData.setSrcport(IpUtils.getRandom(65535));
            flowData.setDstport(IpUtils.getRandom(65535));
            flowData.setProt(getProtoPort(r));
            flowData.setAppProt(getProtoStr(r));
            flowData.setSrcMask("0");
            flowData.setDstMask("0");
            flowData.setdOctets(IpUtils.getRandom(1000000));
            flowData.setNexthop("0.0.0.0");
            list.add(flowData);
        }
        return list;
    }
    private static int getProtoPort(int r){
        int result = 6;
        switch (r%4){
            case 1 : result = 1 ;
            break;
            case 2 : result = 2 ;
                break;
            case 3 : result = 6 ;
                break;
            case 4 : result = 17 ;
                break;
        }
        return result;
    }
    private static String getProtoStr(int r){
        String result = "TCP";
        switch (r%4){
            case 1 : result = "ICMP" ;
                break;
            case 2 : result = "IGMP" ;
                break;
            case 3 : result = "TCP" ;
                break;
            case 4 : result = "UDP" ;
                break;
        }
        return result;
    }

    public static void main(String[] args) {
//        String ip = IpUtils.getRandomIp();
//        System.out.println(ip);
//        System.out.println(UUID.randomUUID());
//        System.out.println(Math.random());
//

        System.out.println(IpUtils.getRandom(4));
    }


}
