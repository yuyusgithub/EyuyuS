package db.model;

/**
 * Created by 彦祖 .
 */
public class FlowData {

    private long lid;

    private String routerIp;// 路由器IP

    private String probe_id;

    private String probe_ip;// 采集器IP

    private String probe_If;//采集器接口

    private int input;// 输入接口的SNMP索引

    private int output;// 输出接口的SNMP索引

    private long collectorId;// 采集器ID

    private long timeFirst;// 统计开始时间

    private long timeLast;// 统计结束时间

    private String srcaddr;// 源IP地址

    private int srcaddrInt;

    private int dstaddrInt;

    private int srcip;// 源IP标记

    private String dstaddr;// 地址

    private int dstip;// 目的IP标记

    private int srcport;// TCP/UDP源端口号或等价值

    private int dstport;// TCP/UDP目的端口号或等价值

    private int prot;// IP协议（例如， 6 = TCP, 17 = UDP）

    private String appProt;// 应用层协议(例如，http，mysql)

    private Integer appProtValue;

    private String srcMask;// 源地址前缀的掩码位

    private String dstMask;// 目的地址前缀的掩码位

    private long dPkts;// 流中的报文个数

    private long dOctets;// 在流的报文中第3层字节总数

    private String nexthop;// 下一条的路由器IP地址

    private int tcpFlags;// TCP标记的累积OR

    private String tos;// 服务的IP类型

    private int srtAs;// 源的AS，原来的或对等的

    private int dstAs;// 目的AS，原来的或对等的

    public String pad1;// 备用1

    public String pad2;// 备用2

    public String pad3;// 备用3

    public String pad4;// 备用4

    public String pad5;// 备用5

    private int isProbeFlow ;

    private String communication;

    //新增字段
    private int cap_len;

    //	u_int32_t  data_len;//记录数据包实际长度
    private int data_len;

    //	u_int32_t  ip_len;//记录ip层数据长度
    private int ip_len;

    //	u_short vlanId;//识别vlan id
    private int vlanId;

    //	u_char srcMacAddress[6]; //源mac
    private String srcMacAddress;

    //	u_char dstMacAddress[6];//目的mac
    private String dstMacAddress;

    private String sign_app;

    private String sign_net;

    public long getLid() {
        return lid;
    }

    public void setLid(long lid) {
        this.lid = lid;
    }

    public String getRouterIp() {
        return routerIp;
    }

    public void setRouterIp(String routerIp) {
        this.routerIp = routerIp;
    }

    public String getProbe_id() {
        return probe_id;
    }

    public void setProbe_id(String probe_id) {
        this.probe_id = probe_id;
    }

    public String getProbe_ip() {
        return probe_ip;
    }

    public void setProbe_ip(String probe_ip) {
        this.probe_ip = probe_ip;
    }

    public String getProbe_If() {
        return probe_If;
    }

    public void setProbe_If(String probe_If) {
        this.probe_If = probe_If;
    }

    public int getInput() {
        return input;
    }

    public void setInput(int input) {
        this.input = input;
    }

    public int getOutput() {
        return output;
    }

    public void setOutput(int output) {
        this.output = output;
    }

    public long getCollectorId() {
        return collectorId;
    }

    public void setCollectorId(long collectorId) {
        this.collectorId = collectorId;
    }

    public long getTimeFirst() {
        return timeFirst;
    }

    public void setTimeFirst(long timeFirst) {
        this.timeFirst = timeFirst;
    }

    public long getTimeLast() {
        return timeLast;
    }

    public void setTimeLast(long timeLast) {
        this.timeLast = timeLast;
    }

    public String getSrcaddr() {
        return srcaddr;
    }

    public void setSrcaddr(String srcaddr) {
        this.srcaddr = srcaddr;
    }

    public int getSrcaddrInt() {
        return srcaddrInt;
    }

    public void setSrcaddrInt(int srcaddrInt) {
        this.srcaddrInt = srcaddrInt;
    }

    public int getDstaddrInt() {
        return dstaddrInt;
    }

    public void setDstaddrInt(int dstaddrInt) {
        this.dstaddrInt = dstaddrInt;
    }

    public int getSrcip() {
        return srcip;
    }

    public void setSrcip(int srcip) {
        this.srcip = srcip;
    }

    public String getDstaddr() {
        return dstaddr;
    }

    public void setDstaddr(String dstaddr) {
        this.dstaddr = dstaddr;
    }

    public int getDstip() {
        return dstip;
    }

    public void setDstip(int dstip) {
        this.dstip = dstip;
    }

    public int getSrcport() {
        return srcport;
    }

    public void setSrcport(int srcport) {
        this.srcport = srcport;
    }

    public int getDstport() {
        return dstport;
    }

    public void setDstport(int dstport) {
        this.dstport = dstport;
    }

    public int getProt() {
        return prot;
    }

    public void setProt(int prot) {
        this.prot = prot;
    }

    public String getAppProt() {
        return appProt;
    }

    public void setAppProt(String appProt) {
        this.appProt = appProt;
    }

    public Integer getAppProtValue() {
        return appProtValue;
    }

    public void setAppProtValue(Integer appProtValue) {
        this.appProtValue = appProtValue;
    }

    public String getSrcMask() {
        return srcMask;
    }

    public void setSrcMask(String srcMask) {
        this.srcMask = srcMask;
    }

    public String getDstMask() {
        return dstMask;
    }

    public void setDstMask(String dstMask) {
        this.dstMask = dstMask;
    }

    public long getdPkts() {
        return dPkts;
    }

    public void setdPkts(long dPkts) {
        this.dPkts = dPkts;
    }

    public long getdOctets() {
        return dOctets;
    }

    public void setdOctets(long dOctets) {
        this.dOctets = dOctets;
    }

    public String getNexthop() {
        return nexthop;
    }

    public void setNexthop(String nexthop) {
        this.nexthop = nexthop;
    }

    public int getTcpFlags() {
        return tcpFlags;
    }

    public void setTcpFlags(int tcpFlags) {
        this.tcpFlags = tcpFlags;
    }

    public String getTos() {
        return tos;
    }

    public void setTos(String tos) {
        this.tos = tos;
    }

    public int getSrtAs() {
        return srtAs;
    }

    public void setSrtAs(int srtAs) {
        this.srtAs = srtAs;
    }

    public int getDstAs() {
        return dstAs;
    }

    public void setDstAs(int dstAs) {
        this.dstAs = dstAs;
    }

    public String getPad1() {
        return pad1;
    }

    public void setPad1(String pad1) {
        this.pad1 = pad1;
    }

    public String getPad2() {
        return pad2;
    }

    public void setPad2(String pad2) {
        this.pad2 = pad2;
    }

    public String getPad3() {
        return pad3;
    }

    public void setPad3(String pad3) {
        this.pad3 = pad3;
    }

    public String getPad4() {
        return pad4;
    }

    public void setPad4(String pad4) {
        this.pad4 = pad4;
    }

    public String getPad5() {
        return pad5;
    }

    public void setPad5(String pad5) {
        this.pad5 = pad5;
    }

    public int getIsProbeFlow() {
        return isProbeFlow;
    }

    public void setIsProbeFlow(int isProbeFlow) {
        this.isProbeFlow = isProbeFlow;
    }

    public String getCommunication() {
        return communication;
    }

    public void setCommunication(String communication) {
        this.communication = communication;
    }

    public int getCap_len() {
        return cap_len;
    }

    public void setCap_len(int cap_len) {
        this.cap_len = cap_len;
    }

    public int getData_len() {
        return data_len;
    }

    public void setData_len(int data_len) {
        this.data_len = data_len;
    }

    public int getIp_len() {
        return ip_len;
    }

    public void setIp_len(int ip_len) {
        this.ip_len = ip_len;
    }

    public int getVlanId() {
        return vlanId;
    }

    public void setVlanId(int vlanId) {
        this.vlanId = vlanId;
    }

    public String getSrcMacAddress() {
        return srcMacAddress;
    }

    public void setSrcMacAddress(String srcMacAddress) {
        this.srcMacAddress = srcMacAddress;
    }

    public String getDstMacAddress() {
        return dstMacAddress;
    }

    public void setDstMacAddress(String dstMacAddress) {
        this.dstMacAddress = dstMacAddress;
    }

    public String getSign_app() {
        return sign_app;
    }

    public void setSign_app(String sign_app) {
        this.sign_app = sign_app;
    }

    public String getSign_net() {
        return sign_net;
    }

    public void setSign_net(String sign_net) {
        this.sign_net = sign_net;
    }
}
