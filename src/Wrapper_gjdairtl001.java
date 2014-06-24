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


public class Wrapper_gjdairtl001 implements QunarCrawler {

	public static void main(String[] args) {
		FlightSearchParam searchParam = new FlightSearchParam();
		searchParam.setDep("BME");
		searchParam.setArr("KNX");
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
			
			String dateStr = getDataStr(param.getDepDate());
			String getUrl = String.format("https://secure.airnorth.com.au/AirnorthIBE/availprocessing.aspx?triptype=o&port.0=%s&port.1=%s&date.0=%s&date.1=%s&pax.0=1&pax.1=0&pax.2=0&", param.getDep(), param.getArr(), dateStr, dateStr);
		
			get = new QFGetMethod(getUrl);
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
		if (html.contains("\"Result\":false")){
			result.setRet(true);
			result.setStatus(Constants.NO_RESULT);
			return result;	
		}		
		
		try{
		
			List<OneWayFlightInfo> flightList = new ArrayList<OneWayFlightInfo>();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date date = format.parse(param.getDepDate());
		
	        Pattern p = null;  
	        Matcher ma = null;			
			// ulÕýÔò  
//	        String regex = "<ul class=\"d2_9\">([\\s\\S]*<li>)<a.*href='(.*)'.*>(.+?)</a> \\[(.*)\\]</li>([\\s].*)";  
	        String regex = "<td class=\"Date\">(.*)<br />(.*)</td><td class=\"PortName\">(.*)<br />(.*)</td><td class=\"PortName\">(.*)<br />(.*)</td><td class=\"FlightNumber\">(.*)</td><td class=\"Fare fare-family-1\" rowspan=\"1\"><input id=(.*)<br />(.*)</label></td><td class=\"Fare fare-family-2\" rowspan=\"1\"><input id=(.*)<br />(.*)</label></td><td class=\"Fare fare-family-3\" rowspan=\"1\"><input id=(.*)<br />(.*)</label></td>";  
	        p = Pattern.compile(regex);  
	        ma = p.matcher(html);  
	        // ul×Ö·û´®  
	        while (ma.find()) {
				OneWayFlightInfo flight = new OneWayFlightInfo();
				FlightDetail flightDetail = new FlightDetail();
				List<String> flightNoList = new ArrayList<String>();
				flightNoList.add(ma.group(7));
				flightDetail.setArrcity(param.getArr());
				flightDetail.setDepcity(param.getDep());
				flightDetail.setDepdate(date);
				flightDetail.setFlightno(flightNoList);
				String price = ma.group(9);
				String[] pricesp =price.split(" ");
				String monetaryUnit = "CNY";
				if ("$".equals(pricesp[0]))
					monetaryUnit = "USD";
				flightDetail.setMonetaryunit(monetaryUnit);
				flightDetail.setPrice(Math.round(Float.valueOf(pricesp[1])));
				flightDetail.setTax(0);
				
				List<FlightSegement> segs = new ArrayList<FlightSegement>();
				FlightSegement seg = new FlightSegement();
				seg.setDeptime(ma.group(4));
				seg.setDepairport(param.getDep());
				seg.setArrtime(ma.group(6));
				seg.setArrairport(param.getArr());
				
				segs.add(seg);
				
				flight.setDetail(flightDetail);
				flight.setInfo(segs);
				
				flightList.add(flight);
	        }  			
			if(flightList.size()==0){
				result.setRet(false);
				result.setStatus(Constants.PARSING_FAIL);
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
		map.put("cur", "HKD");
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
	File f = new File("D:/log/" + fileName + ".txt");
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
