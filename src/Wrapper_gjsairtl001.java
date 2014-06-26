import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.qunar.qfwrapper.bean.booking.BookingInfo;
import com.qunar.qfwrapper.bean.booking.BookingResult;
import com.qunar.qfwrapper.bean.search.FlightDetail;
import com.qunar.qfwrapper.bean.search.FlightSearchParam;
import com.qunar.qfwrapper.bean.search.FlightSegement;
import com.qunar.qfwrapper.bean.search.OneWayFlightInfo;
import com.qunar.qfwrapper.bean.search.ProcessResultInfo;
import com.qunar.qfwrapper.bean.search.RoundTripFlightInfo;
import com.qunar.qfwrapper.constants.Constants;
import com.qunar.qfwrapper.interfaces.QunarCrawler;
import com.qunar.qfwrapper.util.QFGetMethod;
import com.qunar.qfwrapper.util.QFHttpClient;
import com.travelco.rdf.infocenter.InfoCenter;


public class Wrapper_gjsairtl001 implements QunarCrawler {

	public static void main(String[] args) {
		FlightSearchParam searchParam = new FlightSearchParam();
		searchParam.setDep("BME");
		searchParam.setArr("PHE");
		searchParam.setDepDate("2014-9-19");
		searchParam.setRetDate("2014-9-23");
		searchParam.setTimeOut("60000");
		searchParam.setToken("");
		Wrapper_gjsairtl001 wrap = new Wrapper_gjsairtl001();
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
			String depDateStr = getDataStr(param.getDepDate());
			String retDateStr = getDataStr(param.getRetDate());
			String getUrl =  String.format("https://secure.airnorth.com.au/AirnorthIBE/availprocessing.aspx?triptype=r&port.0=%s&port.1=%s&date.0=%s&date.1=%s&pax.0=1&pax.1=0&pax.2=0&", param.getDep(), param.getArr(), depDateStr, retDateStr);
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
		if (html.contains("there are no flights that match your selection") || html.contains("There are no flights on the day you selected") || html.contains("we were not able to find any seats on the day you selected")){
			result.setRet(true);
			result.setStatus(Constants.NO_RESULT);
			return result;	
		}		
		try{
			List<RoundTripFlightInfo> roundTripFlightList = new ArrayList<RoundTripFlightInfo>();//往返组合
			List<OneWayFlightInfo> outboundFlightList = new ArrayList<OneWayFlightInfo>();//去程列表
			List<OneWayFlightInfo> returnedFlightList = new ArrayList<OneWayFlightInfo>();//返程列表
			
	        //匹配去程
//	        String regex = "Flights Out</h3>(.*)<div id=\"ctl00_ContentPlaceHolder1_pnlInboundFlights\">";  
	        String goInfo = StringUtils.substringBetween(html, "Flights Out", "</table>");
	        genFlightList(outboundFlightList, goInfo, param);
	        //匹配返程
	        String backInfo = StringUtils.substringBetween(html, "Flights Back", "</table>");
	        genFlightList(returnedFlightList, backInfo, param);
			
			
			//当去程或回程无数据时返回NO_RESULT
			if(outboundFlightList.size()==0||returnedFlightList.size()==0){
				result.setRet(true);
				result.setStatus(Constants.NO_RESULT);
				return result;
			}			
			//两层循环，对去程和返程list做笛卡尔积得到组合后的所有往返航程
			for(OneWayFlightInfo obfl:outboundFlightList){
				for(OneWayFlightInfo rtfl:returnedFlightList){
					RoundTripFlightInfo round = new RoundTripFlightInfo();
					round.setInfo(obfl.getInfo());//去程航段信息
					round.setOutboundPrice(obfl.getDetail().getPrice());//去程价格
					round.setReturnedPrice(rtfl.getDetail().getPrice());//返程价格
					FlightDetail detail = new FlightDetail();
					detail = obfl.getDetail();
					detail.setPrice(sum(obfl.getDetail().getPrice(),rtfl.getDetail().getPrice()));//往返总价格
					//detail.setPrice(obfl.getDetail().getPrice()+rtfl.getDetail().getPrice());//往返总价格
					//detail.setTax(obfl.getDetail().getTax()+rtfl.getDetail().getTax());//往返总税费
					detail.setTax(sum(obfl.getDetail().getTax(),rtfl.getDetail().getTax()));
					round.setDetail(detail);				//将设置后的去程信息装入往返中
					round.setRetdepdate(rtfl.getDetail().getDepdate());//返程日期
					round.setRetflightno(rtfl.getDetail().getFlightno());//返程航班号list
					round.setRetinfo(rtfl.getInfo());//返程信息
					roundTripFlightList.add(round);//添加到list
				}
			}			

			result.setRet(true);
			result.setStatus(Constants.SUCCESS);
			result.setData(roundTripFlightList);		
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
		String depDateStr = getDataStr(param.getDepDate());
		String retDateStr = getDataStr(param.getRetDate());
		String bookingUrlPre = "https://secure.airnorth.com.au/AirnorthIBE/availprocessing.aspx";
		BookingResult bookingResult = new BookingResult();
		
		BookingInfo bookingInfo = new BookingInfo();
		bookingInfo.setAction(bookingUrlPre);
		bookingInfo.setMethod("get");
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("triptype", "r");
		map.put("port.0", param.getDep());
		map.put("port.1", param.getArr());
		map.put("date.0", depDateStr);
		map.put("date.1", retDateStr);
		map.put("pax.0", "1");
		map.put("pax.1", "0");
		map.put("pax.2", "0");
		bookingInfo.setInputs(map);		
		bookingResult.setData(bookingInfo);
		bookingResult.setRet(true);
		return bookingResult;
	}

	/**
	 * 生成OneWayFlightInfo 信息
	 * @param outboundFlightList
	 * @param ma
	 * @throws ParseException 
	 */
	private void genFlightList(List<OneWayFlightInfo> flightList, String flightInfo, FlightSearchParam param) throws ParseException {
		if (StringUtils.isBlank(flightInfo))
			return;
		Pattern ptr = null;
		Matcher mtr = null;
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//		Date date = format.parse(param.getDepDate());
		
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
	        boolean hasAirSale = false;
	        if (trString.contains("fare-family-0")){
	        	regex = "<td class=\"Date\">(.*)<br />(.*)</td><td class=\"PortName\">(.*)<br />(.*)</td><td class=\"PortName\">(.*)<br />(.*)</td><td class=\"FlightNumber\">(.*)</td><td class=\"Fare fare-family-0\" rowspan=\"([0-9])\">(.*)</td><td class=\"Fare fare-family-1\" rowspan=\"([0-9])\">(.*)</td><td class=\"Fare fare-family-2\" rowspan=\"([0-9])\">(.*)</td><td class=\"Fare fare-family-3\" rowspan=\"([0-9])\">(.*)</td>";  
	        	hasAirSale = true;
	        }
	        else
	        	regex = "<td class=\"Date\">(.*)<br />(.*)</td><td class=\"PortName\">(.*)<br />(.*)</td><td class=\"PortName\">(.*)<br />(.*)</td><td class=\"FlightNumber\">(.*)</td><td class=\"Fare fare-family-1\" rowspan=\"([0-9])\">(.*)</td><td class=\"Fare fare-family-2\" rowspan=\"([0-9])\">(.*)</td><td class=\"Fare fare-family-3\" rowspan=\"([0-9])\">(.*)</td>";  
        	p = Pattern.compile(regex);  
        	ma = p.matcher(trString);
        	if (ma.find()){
        		flight = new OneWayFlightInfo();
				flightDetail = new FlightDetail();
        		segs = new ArrayList<FlightSegement>();
        		
        		String deptDate = ma.group(2);
    			String deptPort = ma.group(3);
	        	String deptTime = ma.group(4);
    			String arrPort = ma.group(5);
	        	String arrTime = ma.group(6);
	        	String flightNo = ma.group(7);
	        	String airLv1 = ma.group(9);
	        	String airLv2 = ma.group(11);
	        	String airLv3 = ma.group(13);
	        	String airLv4 = "";
	        	if (hasAirSale)
	        		airLv4 = ma.group(15);
	        	deptDate = getFlightDate(deptDate);
	        	deptPort = InfoCenter.getCityCodeFromCity(deptPort);
	        	arrPort = InfoCenter.getCityCodeFromCity(arrPort);
				List<String> flightNoList = new ArrayList<String>();
				flightNoList.add(flightNo);
				flightDetail.setWrapperid("gjdairtl001");
				flightDetail.setArrcity(param.getArr());
				flightDetail.setDepcity(param.getDep());
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Date date = format.parse(deptDate);
				
				flightDetail.setDepdate(date);
				flightDetail.setFlightno(flightNoList);
				String[] priceInfo = getPriceInfo(airLv1, airLv2, airLv3, airLv4);
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
				seg.setDepDate(deptDate);
				seg.setArrDate(deptDate); 
				segs.add(seg);
				
				flight.setDetail(flightDetail);
				flight.setInfo(segs);
				flightList.add(flight);
        		
	        	
        	}
        	else{
//        		regex = "<td class=\"Date\">(.*)<br />(.*)</td><td class=\"PortName\">(.*)<br />(.*)</td><td class=\"PortName\">(.*)<br />(.*)</td>";
        		regex = "<td class=\"Date\">(.*)<br />(.*)</td><td class=\"PortName\">(.*)<br />(.*)</td><td class=\"PortName\">(.*)<br />(.*)</td><td class=\"FlightNumber\">(.*)</td>";
        		p = Pattern.compile(regex);  
        		ma = p.matcher(trString);
        		if (ma.find()){
        			if (segs == null)
        				continue;
        			String deptDate = ma.group(2);
        			String deptPort = ma.group(3);
		        	String deptTime = ma.group(4);
        			String arrPort = ma.group(5);
		        	String arrTime = ma.group(6);
		        	String flightNo = ma.group(7);
		        	deptDate = getFlightDate(deptDate);
		        	deptPort = InfoCenter.getCityCodeFromCity(deptPort);
		        	arrPort = InfoCenter.getCityCodeFromCity(arrPort);
					FlightSegement seg = new FlightSegement();
					seg.setDeptime(deptTime);
					seg.setDepairport(deptPort);
					seg.setArrtime(arrTime);
					seg.setArrairport(arrPort);
					seg.setFlightno(flightNo);
					seg.setDepDate(deptDate);
					seg.setArrDate(deptDate); 
					flightDetail.getFlightno().add(flightNo);
					segs.add(seg);
        		}
        	}
    	}
	}

	private String[] getPriceInfo(String airSale, String airWeb, String airSaver, String airFlex) {
		String airPriceInfo = null;
		if (airSale.startsWith("<input"))
			airPriceInfo = airSale;
		else if (airWeb.startsWith("<input"))
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
	
	/**
	 * @param dateStr    13 SEP 2004
	 * @return
	 */
	private String getFlightDate(String dateStr) {
		String[] dataStrs = dateStr.split(" ");
		String day = dataStrs[0];
		String month = dataStrs[1];
		if (month.toUpperCase().equals("JAN"))
			month = "01";
		else if (month.toUpperCase().equals("FEB"))
			month = "02";
		else if (month.toUpperCase().equals("MAR"))
			month = "03";
		else if (month.toUpperCase().equals("APR"))
			month = "04";
		else if (month.toUpperCase().equals("MAY"))
			month = "05";
		else if (month.toUpperCase().equals("JUN"))
			month = "06";
		else if (month.toUpperCase().equals("JUL"))
			month = "07";
		else if (month.toUpperCase().equals("AUG"))
			month = "08";
		else if (month.toUpperCase().equals("SEP"))
			month = "09";
		else if (month.toUpperCase().equals("OCT"))
			month = "10";
		else if (month.toUpperCase().equals("NOV"))
			month = "11";
		else if (month.toUpperCase().equals("DEC"))
			month = "12";
		String year = dataStrs[2];
		String formatDate = year +  '-' + month + "-" + day;
		return formatDate;
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

	//计算相加，避免丢失精度
	private double sum(double d1,double d2){
		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.add(bd2).doubleValue(); 
	}
	//计算相加，避免丢失精度
	private double sum(double d1,double d2,double d3){
		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        BigDecimal bd3 = new BigDecimal(Double.toString(d3));
        return bd1.add(bd2).add(bd3).doubleValue(); 
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
