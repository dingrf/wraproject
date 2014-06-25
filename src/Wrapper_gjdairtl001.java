import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.security.KeyManagementException;  
import java.security.KeyStore;  
import java.security.NoSuchAlgorithmException;  
import java.security.cert.CertificateException;  
import java.security.cert.X509Certificate; 

import javax.net.ssl.SSLContext;  
import javax.net.ssl.TrustManager;  
import javax.net.ssl.X509TrustManager; 

import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.lang.StringUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.qunar.qfwrapper.bean.booking.BookingInfo;
import com.qunar.qfwrapper.bean.booking.BookingResult;
import com.qunar.qfwrapper.bean.search.FlightDetail;
import com.qunar.qfwrapper.bean.search.FlightSearchParam;
import com.qunar.qfwrapper.bean.search.FlightSegement;
import com.qunar.qfwrapper.bean.search.OneWayFlightInfo;
import com.qunar.qfwrapper.bean.search.ProcessResultInfo;
import com.qunar.qfwrapper.constants.Constants;
import com.qunar.qfwrapper.interfaces.QunarCrawler;
import com.qunar.qfwrapper.util.QFGetMethod;
import com.qunar.qfwrapper.util.QFHttpClient;
import com.travelco.rdf.infocenter.InfoCenter;


public class Wrapper_gjdairtl001 implements QunarCrawler {

	public static void main(String[] args) {
//		String str = InfoCenter.getCityCodeFromCity("broome");
		FlightSearchParam searchParam = new FlightSearchParam();
		searchParam.setDep("BME");
		searchParam.setArr("KNX"); // PHE
		searchParam.setDepDate("2014-09-19");
		searchParam.setTimeOut("60000");
		searchParam.setToken("");
		Wrapper_gjdairtl001 wrap = new Wrapper_gjdairtl001();
		String html = wrap.getHtml(searchParam);
		wrap.saveLog2Txt(html);
		ProcessResultInfo resultInfo = wrap.process(html, searchParam);
	}
	
	@Override
	public String getHtml(FlightSearchParam param) {
		QFGetMethod get = null;	
		try {	
			QFHttpClient httpClient = new QFHttpClient(param, false);
//			httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);

			
////	        HttpClient httpClient = new DefaultHttpClient(); //创建默认的httpClient实例    
//	        X509TrustManager xtm = new X509TrustManager(){   //创建TrustManager    
//	            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}    
//	            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}    
//	            public X509Certificate[] getAcceptedIssuers() {   
//	                return null;   //return new java.security.cert.X509Certificate[0];    
//	            }  
//	        };    
//            //TLS1.0与SSL3.0基本上没有太大的差别，可粗略理解为TLS是SSL的继承者，但它们使用的是相同的SSLContext    
//            SSLContext ctx = SSLContext.getInstance("TLS");    
//                
//            //使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用    
//            ctx.init(null, new TrustManager[]{xtm}, null);   
//                
//            //创建SSLSocketFactory    
//            SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);    
//                
//            //通过SchemeRegistry将SSLSocketFactory注册到我们的HttpClient上
//            ((ClientConnectionManager)httpClient.getHttpConnectionManager()).getSchemeRegistry().register(new Scheme("https", 443, socketFactory));
////            httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));			
//			
			String dateStr = getDataStr(param.getDepDate());
			String getUrl =  String.format("https://secure.airnorth.com.au/AirnorthIBE/availprocessing.aspx?triptype=o&port.0=%s&port.1=%s&date.0=%s&date.1=%s&pax.0=1&pax.1=0&pax.2=0&", param.getDep(), param.getArr(), dateStr, dateStr);
			get = new QFGetMethod(getUrl);
			String cookie = "SESSe80f564800f7c85c3f23730445dc3b31=7f8f0d014742c3545491e94b8d9caf8b;";
			get.addRequestHeader("Cookie", cookie);
			get.addRequestHeader("Referer", "http://www.airnorth.com.au/");
		    httpClient.executeMethod(get);
		    return get.getResponseBodyAsString();
		} catch (Exception e) {			
			e.printStackTrace();
		} finally{
			if (null != get){
				get.releaseConnection();
			}	
		}
		return "Exception";
	}


	@Override
	public ProcessResultInfo process(String html, FlightSearchParam param) {
		ProcessResultInfo result = new ProcessResultInfo();
		if ("Exception".equals(html)) {
			result.setRet(false);
			result.setStatus(Constants.CONNECTION_FAIL);
			return result;	
		}
		if (html.contains("There are no flights on the day you selected") || html.contains("we were not able to find any seats on the day you selected")){
			result.setRet(true);
			result.setStatus(Constants.NO_RESULT);
			return result;	
		}		
		try{
			List<OneWayFlightInfo> flightList = new ArrayList<OneWayFlightInfo>();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date date = format.parse(param.getDepDate());
		
			String flightInfo = StringUtils.substringBetween(html, "Flights Out", "</table>");
			Pattern ptr = null;
			Matcher mtr = null;
			//找到行信息
			String regtr = "<td class=\"Date\">(.*)</td>"; 
			ptr = Pattern.compile(regtr);
			mtr = ptr.matcher(flightInfo);
			List<FlightSegement> segs = null;
			OneWayFlightInfo flight = null;
			FlightDetail flightDetail = null;
			while(mtr.find()){
				String trString = mtr.group(0);
		        Pattern p = null;  
		        Matcher ma = null;			
		        String regex = null;
	        	regex = "<td class=\"Date\">(.*)<br />(.*)</td><td class=\"PortName\">(.*)<br />(.*)</td><td class=\"PortName\">(.*)<br />(.*)</td><td class=\"FlightNumber\">(.*)</td><td class=\"Fare fare-family-1\" rowspan=\"([0-9])\">(.*)</td><td class=\"Fare fare-family-2\" rowspan=\"([0-9])\">(.*)</td><td class=\"Fare fare-family-3\" rowspan=\"([0-9])\">(.*)</td>";  
	        	p = Pattern.compile(regex);  
	        	ma = p.matcher(trString);
	        	if (ma.find()){
	        		flight = new OneWayFlightInfo();
					flightDetail = new FlightDetail();
	        		segs = new ArrayList<FlightSegement>();
	        		
        			String deptPort = ma.group(3);
		        	String deptTime = ma.group(4);
        			String arrPort = ma.group(5);
		        	String arrTime = ma.group(6);
		        	String flightNo = ma.group(7);
		        	String airWeb = ma.group(9);
		        	String airSaver = ma.group(11);
		        	String airFlex = ma.group(13);
		        	deptPort = InfoCenter.getCityCodeFromCity(deptPort);
		        	arrPort = InfoCenter.getCityCodeFromCity(arrPort);
					List<String> flightNoList = new ArrayList<String>();
					flightNoList.add(flightNo);
					flightDetail.setWrapperid("gjdairtl001");
					flightDetail.setArrcity(param.getArr());
					flightDetail.setDepcity(param.getDep());
					flightDetail.setDepdate(date);
					flightDetail.setFlightno(flightNoList);
					String[] priceInfo = getPriceInfo(airWeb, airSaver, airFlex);
					if (priceInfo == null){
						segs = null;
						continue;
					}
					flightDetail.setMonetaryunit(priceInfo[0]);
					flightDetail.setPrice(Double.parseDouble(priceInfo[1]));
					flightDetail.setTax(0);
					
					FlightSegement seg = new FlightSegement();
					seg.setDeptime(deptTime);
					seg.setDepairport(deptPort);
					seg.setArrtime(arrTime);
					seg.setArrairport(arrPort);
					seg.setFlightno(flightNo);
					seg.setDepDate(param.getDepDate());
					seg.setArrDate(param.getDepDate());
					segs.add(seg);
					
					flight.setDetail(flightDetail);
					flight.setInfo(segs);
					flightList.add(flight);
	        		
		        	
	        	}
	        	else{
//	        		regex = "<td class=\"Date\">(.*)<br />(.*)</td><td class=\"PortName\">(.*)<br />(.*)</td><td class=\"PortName\">(.*)<br />(.*)</td>";
	        		regex = "<td class=\"Date\">(.*)<br />(.*)</td><td class=\"PortName\">(.*)<br />(.*)</td><td class=\"PortName\">(.*)<br />(.*)</td><td class=\"FlightNumber\">(.*)</td>";
	        		p = Pattern.compile(regex);  
	        		ma = p.matcher(trString);
	        		if (ma.find()){
	        			if (segs == null)
	        				continue;
	        			String deptPort = ma.group(3);
			        	String deptTime = ma.group(4);
	        			String arrPort = ma.group(5);
			        	String arrTime = ma.group(6);
			        	String flightNo = ma.group(7);
			        	deptPort = InfoCenter.getCityCodeFromCity(deptPort);
			        	arrPort = InfoCenter.getCityCodeFromCity(arrPort);
						FlightSegement seg = new FlightSegement();
						seg.setDeptime(deptTime);
						seg.setDepairport(deptPort);
						seg.setArrtime(arrTime);
						seg.setArrairport(arrPort);
						seg.setFlightno(flightNo);
						seg.setDepDate(param.getDepDate());
						seg.setArrDate(param.getDepDate()); 
						flightDetail.getFlightno().add(flightNo);
						segs.add(seg);
	        		}
	        	}
        	}
			if(flightList.size()==0){
				result.setRet(false);
				result.setStatus(Constants.NO_RESULT);
				return result;
			}
			result.setRet(true);
			result.setStatus(Constants.SUCCESS);
			result.setData(flightList);		
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.setRet(false);
			result.setStatus(Constants.PARSING_FAIL);
			return result;
		}
	}


	@Override
	public BookingResult getBookingInfo(FlightSearchParam param) {
		String dateStr = getDataStr(param.getDepDate());
		String bookingUrlPre = "https://secure.airnorth.com.au/AirnorthIBE/availprocessing.aspx";
		BookingResult bookingResult = new BookingResult();
		
		BookingInfo bookingInfo = new BookingInfo();
		bookingInfo.setAction(bookingUrlPre);
		bookingInfo.setMethod("get");
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("triptype", "o");
		map.put("port.0", param.getDep());
		map.put("port.1", param.getArr());
		map.put("date.0", dateStr);
		map.put("date.1", dateStr);
		map.put("pax.0", "1");
		map.put("pax.1", "0");
		map.put("pax.2", "0");
		bookingInfo.setInputs(map);		
		bookingResult.setData(bookingInfo);
		bookingResult.setRet(true);
		return bookingResult;
	}

	private String[] getPriceInfo(String airWeb, String airSaver, String airFlex) {
		String airPriceInfo = null;
		if (airWeb.startsWith("<input"))
			airPriceInfo = airWeb;
		else if (airSaver.startsWith("<input"))
			airPriceInfo = airSaver;
		else if (airFlex.startsWith("<input"))
			airPriceInfo = airFlex;
		if (airPriceInfo == null)
			return null;
        Pattern p = null;  
        Matcher ma = null;			
        String regex = "<input id=(.*)<br />(.*)</label>";  
        p = Pattern.compile(regex);  
        ma = p.matcher(airPriceInfo);  
		if (ma.find()){
			String[] priceInfo = ma.group(2).split(" ");
			if (priceInfo.length != 2)
				return null;
			if ("$".equals(priceInfo[0]))
				priceInfo[0] = "USD";
			return priceInfo;
		}
		return null;
}
	
	
	private String getDataStr(String depDate) {
		String[] dataStrs = depDate.split("-");
		Integer month = Integer.valueOf(dataStrs[1]); 
		Integer day = Integer.valueOf(dataStrs[2]);
		String monStr = null;
		switch(month){
			case 1:{
				monStr = "JAN";
				break;
			}
			case 2:{
				monStr = "FEB";
				break;
			}
			case 3:{
				monStr = "MAR";
				break;
			}
			case 4:{
				monStr = "APR";
				break;
			}
			case 5:{
				monStr = "MAY";
				break;
			}
			case 6:{
				monStr = "JUN";
				break;
			}
			case 7:{
				monStr = "JUL";
				break;
			}
			case 8:{
				monStr = "AUG";
				break;
			}
			case 9:{
				monStr = "SEP";
				break;
			}
			case 10:{
				monStr = "OCT";
				break;
			}
			case 11:{
				monStr = "NOV";
				break;
			}
			case 12:{
				monStr = "DEC";
				break;
			}
		}
		return day+monStr;
	}

	private void saveLog2Txt(String text) {
	String fileName = String.valueOf(System.currentTimeMillis());
	File f = new File("D:/log/" + fileName + ".html");
	if (!f.exists())
		try {
			File dir = new File(f.getParent());
			if(!dir.exists())
				dir.mkdirs();
			f.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
	BufferedWriter bf = null;
	FileOutputStream fos = null;
	OutputStreamWriter osw = null;
	try{
		fos = new FileOutputStream(f);
		osw = new OutputStreamWriter(fos, "UTF-8");
		bf = new BufferedWriter(osw);
		bf.write(text);
		bf.flush();
	}catch(IOException e){
		e.printStackTrace();
	}finally{
		try {
			if (bf != null)
				bf.close();
			if (fos != null)
				fos.close();
			if (osw != null)
				osw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}	
	
}
