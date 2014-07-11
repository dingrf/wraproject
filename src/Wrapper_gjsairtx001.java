import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import com.qunar.qfwrapper.util.QFHttpClient;
import com.qunar.qfwrapper.util.QFPostMethod;
//import com.travelco.rdf.infocenter.InfoCenter;


public class Wrapper_gjsairtx001 implements QunarCrawler {

	public static void main(String[] args) {
		FlightSearchParam searchParam = new FlightSearchParam();
		searchParam.setDep("QXB");
		searchParam.setArr("SLU"); // SXM
		searchParam.setDepDate("2014-09-13");
		searchParam.setRetDate("2014-09-21");
		searchParam.setTimeOut("60000");
		searchParam.setToken("");
		Wrapper_gjsairtx001 wrap = new Wrapper_gjsairtx001();
		String html = wrap.getHtml(searchParam);
		wrap.saveLog2Txt(html);
//		String jsonStr = StringUtils.substringBetween(html, "var generatedJSon = new String('", "');");
//		String jsonStr = "{\"list_tab\":{\"list_proposed_bound\":[{\"list_flight\":[{\"list_segment\":[{\"b_date_date\":\"20140912\",\"b_location\":{\"city_name\":\"Aix en Provence\",\"location_name\":\"Gare TGV\",\"location_code\":\"QXB\",\"city_code\":\"QXB\",\"country_code\":\"FR\",\"country_name\":\"France\"},\"e_ticketing\":\"true\",\"e_location\":{\"city_name\":\"Paris\",\"location_name\":\"Gare TGV de Massy\",\"location_code\":\"XJY\",\"city_code\":\"PAR\",\"country_code\":\"FR\",\"country_name\":\"France\"},\"e_date_time\":\"1835\",\"flight_number\":\"5542\",\"equipment\":{\"name\":\"Train\",\"code\":\"TRN\"},\"b_date_time\":\"1459\",\"b_date_formatted_time\":\"14:59\",\"segment_id\":\"0\",\"number_of_stops\":\"0\",\"e_date_date\":\"20140912\",\"e_date_formatted_date\":\"ven., 12 septembre\",\"b_date_formatted_date\":\"ven., 12 septembre\",\"e_date_formatted_time\":\"18:35\",\"airline\":{\"name\":\"Air Caraibes\",\"code\":\"TX\"}},{\"b_location\":{\"city_name\":\"Paris\",\"location_name\":\"Orly\",\"location_code\":\"ORY\",\"city_code\":\"PAR\",\"country_code\":\"FR\",\"country_name\":\"France\"},\"b_date_date\":\"20140913\",\"b_terminal\":\"S\",\"e_ticketing\":\"true\",\"e_location\":{\"city_name\":\"Saint Maarten\",\"location_name\":\"Princess Juliana International\",\"location_code\":\"SXM\",\"city_code\":\"SXM\",\"country_code\":\"SX\",\"country_name\":\"Saint-Martin\"},\"elapsed_flying_time\":\"104760000\",\"e_date_time\":\"1405\",\"flight_number\":\"520\",\"equipment\":{\"name\":\"Airbus Industrie A330-200\",\"code\":\"332\"},\"b_date_time\":\"1120\",\"b_date_formatted_time\":\"11:20\",\"b_day_indicator\":\"1\",\"segment_id\":\"1\",\"formatted_elapsed_flying_time\":\"29h06min\",\"number_of_stops\":\"0\",\"e_date_formatted_date\":\"sam., 13 septembre\",\"e_date_date\":\"20140913\",\"e_day_indicator\":\"1\",\"b_date_formatted_date\":\"sam., 13 septembre\",\"e_date_formatted_time\":\"14:05\",\"airline\":{\"name\":\"Air Caraibes\",\"code\":\"TX\"}}],\"flight_id\":\"0\"}]}],\"list_recommendation\":[{\"list_bound\":[{\"list_flight\":[{\"lsa_debug_info\":{\"applied_last_seat_rule\":\"Rule1\",\"rbd\":\"M\",\"first_flight_number\":\"5542\"},\"number_of_last_seats\":\"6\",\"display_last_seats\":\"YES\",\"flight_id\":\"0\"}]}],\"list_trip_price\":[{\"amount\":\"680.11\",\"total_amount\":\"680.11\",\"exchange_rate\":\"1.0\",\"list_fee\":[{\"calculation\":\"0\",\"list_value\":[{\"value_float\":5,\"value\":\"5.0\",\"type\":{\"code\":\"0\"}}],\"occurrence\":\"0\",\"type\":{\"code\":\"8\"}}],\"total_amount_float\":680.11,\"tax\":\"193.11\",\"amount_without_tax\":\"487.0\",\"amount_without_tax_float\":487,\"tax_float\":193.11,\"exchange_rate_float\":1,\"amount_float\":680.11,\"currency\":{\"name\":\"Euro\",\"code\":\"EUR\"}}],\"list_pnr\":[{\"list_traveller_type\":[{\"list_bound\":[{\"list_segment\":[{\"fare_break_point\":\"false\",\"list_ptc_applied\":[{\"code\":\"ADT\"}],\"list_fare_types\":[{\"name\":\"Public\",\"code\":\"RP\"}],\"fare_class\":\"LLOW1\",\"segment_id\":\"0\",\"rbd\":\"M\",\"fare_family\":{\"color\":\"#FFA61A\",\"color_name\":\"CUSTOM\",\"highlighting_color\":\"#FFFFFF\",\"hierarchy\":\"8000\",\"short_name\":\"SOLEIL\",\"brand_name\":\"Soleil\"}},{\"fare_break_point\":\"true\",\"list_ptc_applied\":[{\"code\":\"ADT\"}],\"list_fare_types\":[{\"name\":\"Public\",\"code\":\"RP\"}],\"fare_class\":\"LLOW1\",\"segment_id\":\"1\",\"rbd\":\"L\",\"fare_family\":{\"color\":\"#FFA61A\",\"color_name\":\"CUSTOM\",\"highlighting_color\":\"#FFFFFF\",\"hierarchy\":\"8000\",\"short_name\":\"SOLEIL\",\"brand_name\":\"Soleil\"}}]}],\"list_traveller_price\":[{\"amount\":\"680.11\",\"total_amount\":\"680.11\",\"exchange_rate\":\"1.0\",\"list_fee\":[{\"calculation\":\"0\",\"list_value\":[{\"value_float\":5,\"value\":\"5.0\",\"type\":{\"code\":\"0\"}}],\"occurrence\":\"0\",\"type\":{\"code\":\"8\"}}],\"total_amount_float\":680.11,\"tax\":\"193.11\",\"amount_without_tax\":\"487.0\",\"amount_without_tax_float\":487,\"tax_float\":193.11,\"exchange_rate_float\":1,\"amount_float\":680.11,\"currency\":{\"name\":\"Euro\",\"code\":\"EUR\"}}],\"traveller_type\":{\"name\":\"Adulte\",\"code\":\"ADT\"},\"number\":\"1\",\"list_traveller\":[{\"has_infant\":\"false\",\"requested_traveller_type\":{\"name\":\"Adulte\",\"code\":\"ADT\"},\"is_primary_traveller\":\"true\"}],\"list_traveller_type_price\":[{\"amount\":\"680.11\",\"total_amount\":\"680.11\",\"exchange_rate\":\"1.0\",\"list_fee\":[{\"calculation\":\"0\",\"list_value\":[{\"value_float\":5,\"value\":\"5.0\",\"type\":{\"code\":\"0\"}}],\"occurrence\":\"0\",\"type\":{\"name\":\"BKSERVICE\",\"code\":\"8\"}}],\"total_amount_float\":680.11,\"tax\":\"193.11\",\"amount_without_tax\":\"487.0\",\"amount_without_tax_float\":487,\"tax_float\":193.11,\"exchange_rate_float\":1,\"amount_float\":680.11,\"currency\":{\"name\":\"Euro\",\"code\":\"EUR\"}}]}],\"list_pnr_price\":[{\"amount\":\"680.11\",\"total_amount\":\"680.11\",\"exchange_rate\":\"1.0\",\"list_fee\":[{\"calculation\":\"0\",\"list_value\":[{\"value_float\":5,\"value\":\"5.0\",\"type\":{\"code\":\"0\"}}],\"occurrence\":\"0\",\"type\":{\"code\":\"8\"}}],\"total_amount_float\":680.11,\"tax\":\"193.11\",\"amount_without_tax\":\"487.0\",\"amount_without_tax_float\":487,\"tax_float\":193.11,\"exchange_rate_float\":1,\"amount_float\":680.11,\"currency\":{\"name\":\"Euro\",\"code\":\"EUR\"}}]}],\"formatted_discount_price\":\"\",\"price\":685.11,\"recommendation_id\":\"0\",\"formatted_price\":\"685,11 \\u20AC\",\"list_price\":[{\"formatted_discount_price\":\"\",\"price\":685.11,\"formatted_price\":\"685,11 \\u20AC\"}],\"list_excluded\":[],\"fare_family\":{\"color\":\"#FFA61A\",\"color_name\":\"CUSTOM\",\"highlighting_color\":\"#FFFFFF\",\"hierarchy\":\"8000\",\"short_name\":\"SOLEIL\",\"brand_name\":\"Soleil\"}},{\"list_bound\":[{\"list_flight\":[{\"lsa_debug_info\":{\"applied_last_seat_rule\":\"Rule1\",\"rbd\":\"M\",\"first_flight_number\":\"5542\"},\"number_of_last_seats\":\"6\",\"display_last_seats\":\"YES\",\"flight_id\":\"0\"}]}],\"list_trip_price\":[{\"amount\":\"1004.11\",\"total_amount\":\"1004.11\",\"exchange_rate\":\"1.0\",\"list_fee\":[{\"calculation\":\"0\",\"list_value\":[{\"value_float\":5,\"value\":\"5.0\",\"type\":{\"code\":\"0\"}}],\"occurrence\":\"0\",\"type\":{\"code\":\"8\"}}],\"total_amount_float\":1004.11,\"tax\":\"193.11\",\"amount_without_tax\":\"811.0\",\"amount_without_tax_float\":811,\"tax_float\":193.11,\"exchange_rate_float\":1,\"amount_float\":1004.11,\"currency\":{\"name\":\"Euro\",\"code\":\"EUR\"}}],\"list_pnr\":[{\"list_traveller_type\":[{\"list_bound\":[{\"list_segment\":[{\"fare_break_point\":\"false\",\"list_ptc_applied\":[{\"code\":\"ADT\"}],\"list_fare_types\":[{\"name\":\"Public\",\"code\":\"RP\"}],\"fare_class\":\"ALOW1\",\"segment_id\":\"0\",\"rbd\":\"M\",\"fare_family\":{\"color\":\"#0099CC\",\"color_name\":\"CUSTOM\",\"highlighting_color\":\"#FFFFFF\",\"hierarchy\":\"6000\",\"short_name\":\"CARAIBES\",\"brand_name\":\"Cara&iuml;bes\"}},{\"fare_break_point\":\"true\",\"list_ptc_applied\":[{\"code\":\"ADT\"}],\"list_fare_types\":[{\"name\":\"Public\",\"code\":\"RP\"}],\"fare_class\":\"ALOW1\",\"segment_id\":\"1\",\"rbd\":\"A\",\"fare_family\":{\"color\":\"#0099CC\",\"color_name\":\"CUSTOM\",\"highlighting_color\":\"#FFFFFF\",\"hierarchy\":\"6000\",\"short_name\":\"CARAIBES\",\"brand_name\":\"Cara&iuml;bes\"}}]}],\"list_traveller_price\":[{\"amount\":\"1004.11\",\"total_amount\":\"1004.11\",\"exchange_rate\":\"1.0\",\"list_fee\":[{\"calculation\":\"0\",\"list_value\":[{\"value_float\":5,\"value\":\"5.0\",\"type\":{\"code\":\"0\"}}],\"occurrence\":\"0\",\"type\":{\"code\":\"8\"}}],\"total_amount_float\":1004.11,\"tax\":\"193.11\",\"amount_without_tax\":\"811.0\",\"amount_without_tax_float\":811,\"tax_float\":193.11,\"exchange_rate_float\":1,\"amount_float\":1004.11,\"currency\":{\"name\":\"Euro\",\"code\":\"EUR\"}}],\"traveller_type\":{\"name\":\"Adulte\",\"code\":\"ADT\"},\"number\":\"1\",\"list_traveller\":[{\"has_infant\":\"false\",\"requested_traveller_type\":{\"name\":\"Adulte\",\"code\":\"ADT\"},\"is_primary_traveller\":\"true\"}],\"list_traveller_type_price\":[{\"amount\":\"1004.11\",\"total_amount\":\"1004.11\",\"exchange_rate\":\"1.0\",\"list_fee\":[{\"calculation\":\"0\",\"list_value\":[{\"value_float\":5,\"value\":\"5.0\",\"type\":{\"code\":\"0\"}}],\"occurrence\":\"0\",\"type\":{\"name\":\"BKSERVICE\",\"code\":\"8\"}}],\"total_amount_float\":1004.11,\"tax\":\"193.11\",\"amount_without_tax\":\"811.0\",\"amount_without_tax_float\":811,\"tax_float\":193.11,\"exchange_rate_float\":1,\"amount_float\":1004.11,\"currency\":{\"name\":\"Euro\",\"code\":\"EUR\"}}]}],\"list_pnr_price\":[{\"amount\":\"1004.11\",\"total_amount\":\"1004.11\",\"exchange_rate\":\"1.0\",\"list_fee\":[{\"calculation\":\"0\",\"list_value\":[{\"value_float\":5,\"value\":\"5.0\",\"type\":{\"code\":\"0\"}}],\"occurrence\":\"0\",\"type\":{\"code\":\"8\"}}],\"total_amount_float\":1004.11,\"tax\":\"193.11\",\"amount_without_tax\":\"811.0\",\"amount_without_tax_float\":811,\"tax_float\":193.11,\"exchange_rate_float\":1,\"amount_float\":1004.11,\"currency\":{\"name\":\"Euro\",\"code\":\"EUR\"}}]}],\"formatted_discount_price\":\"\",\"price\":1009.11,\"recommendation_id\":\"1\",\"formatted_price\":\"1\\u00A0009,11 \\u20AC\",\"list_price\":[{\"formatted_discount_price\":\"\",\"price\":1009.11,\"formatted_price\":\"1\\u00A0009,11 \\u20AC\"}],\"list_excluded\":[],\"fare_family\":{\"color\":\"#0099CC\",\"color_name\":\"CUSTOM\",\"highlighting_color\":\"#FFFFFF\",\"hierarchy\":\"6000\",\"short_name\":\"CARAIBES\",\"brand_name\":\"Cara&iuml;bes\"}}],\"list_date\":[{\"date_formatted_time\":\"00:00\",\"date_date\":\"20140912\",\"date_formatted_date\":\"ven., 12 septembre\",\"date_time\":\"0000\"}]}}";
//		jsonStr = "{'a':'1'}";
//		JSONObject ajson = (JSONObject) JSON.parse(jsonStr);
		ProcessResultInfo resultInfo = wrap.process(html, searchParam);
	}
	
	@Override
	public String getHtml(FlightSearchParam param) {
		QFPostMethod post = null;	
		try {	
			QFHttpClient httpClient = new QFHttpClient(param, false);
//			httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");
            Date date = format.parse(param.getDepDate());
            String date1 = param.getDepDate().replaceAll("-","")+"0000";
            String date2 = format2.format(date);			
            Date retDate = format.parse(param.getRetDate());
            String date3 = param.getRetDate().replaceAll("-","")+"0000";
            String date4 = format2.format(retDate);			
			
			post = new QFPostMethod("https://vols.aircaraibes.com/plnext/aircaraibesB2C/Override.action");
			NameValuePair[] names = {
					new NameValuePair("SITE","ACSQACSQ"),
					new NameValuePair("EXTERNAL_ID","TEST"),
					new NameValuePair("TRIP_FLOW","Yes"),
					new NameValuePair("EMBEDDED_TRANSACTION","FlexPricerAvailability"),
					new NameValuePair("LANGUAGE","FR"),
					new NameValuePair("DIRECT_LOGIN","No"),
					new NameValuePair("ARRANGE_BY","N"),
					new NameValuePair("REFRESH","0"),
					new NameValuePair("DATE_RANGE_QUALIFIER_1","C"),
					new NameValuePair("DATE_RANGE_QUALIFIER_2","C"),
					new NameValuePair("ENVIRONMENT","PRODUCTION"),
					new NameValuePair("PRICING_TYPE","O"),
					new NameValuePair("SO_SITE_CHARGEABLE_SEATMAP","TRUE"),
					new NameValuePair("DISPLAY_TYPE","1"),
					new NameValuePair("SO_SITE_ALLOW_SERVICE_FEE","1"),
					new NameValuePair("SO_GL","<?xml version=\"1.0\" encoding=\"iso-8859-1\"?><SO_GL><GLOBAL_LIST mode=\"complete\"><NAME>SITE_SERVICE_FEE</NAME><LIST_ELEMENT><CODE>0</CODE><LIST_VALUE>0</LIST_VALUE><LIST_VALUE>2</LIST_VALUE><LIST_VALUE>5.00</LIST_VALUE><LIST_VALUE>EUR</LIST_VALUE></LIST_ELEMENT></GLOBAL_LIST></SO_GL>"),
					new NameValuePair("TRAVELLER_TYPE_1","ADT"),
					new NameValuePair("TRAVELLER_TYPE_2",""),
					new NameValuePair("TRAVELLER_TYPE_3",""),
					new NameValuePair("TRAVELLER_TYPE_4",""),
					new NameValuePair("TRAVELLER_TYPE_5",""),
					new NameValuePair("TRAVELLER_TYPE_6",""),
					new NameValuePair("TRAVELLER_TYPE_7",""),
					new NameValuePair("TRAVELLER_TYPE_8",""),
					new NameValuePair("TRAVELLER_TYPE_9",""),
					new NameValuePair("HAS_INFANT_1",""),
					new NameValuePair("HAS_INFANT_2",""),
					new NameValuePair("HAS_INFANT_3",""),
					new NameValuePair("HAS_INFANT_4",""),
					new NameValuePair("HAS_INFANT_5",""),
					new NameValuePair("SO_LANG_URL_AIR_NFS_SRCH","http://www.aircaraibes.com/"),
					new NameValuePair("SO_SITE_REDIRECT_MODE","AUTOMATIC"),
					new NameValuePair("SO_SITE_MANUAL_ETKT_CMD","TTP/ITR-EMLA/INVJ-EMLA"),
					new NameValuePair("SO_SITE_MOP_CALL_ME","FALSE"),
					new NameValuePair("SO_SITE_ET_CODE_SHARE","00"),
					new NameValuePair("SO_SITE_ALLOW_PROMO","FALSE"),
					new NameValuePair("SO_SITE_MOP_EXTERNAL_INS","TRUE"),
					new NameValuePair("DIRECT_NON_STOP","FALSE"),
					new NameValuePair("SO_SITE_IS_INSURANCE_ENABLED","TRUE"),
					new NameValuePair("SO_SITE_IS_INS_MERCHANT","FALSE"),
					new NameValuePair("SO_SITE_MOP_EXTERNAL_INS","FALSE"),
					new NameValuePair("SO_SITE_MINIMAL_TIME","H24"),
					new NameValuePair("SO_SITE_NO_CC_ELLIGIBILITY","TRUE"),
					new NameValuePair("SO_SITE_VERIF_INFANT_SSR","TRUE"),
					new NameValuePair("SO_SITE_ALLOW_CS_CODE_SHARE","TRUE"),
					new NameValuePair("SO_SITE_PROMPT_FEE","N"),
					new NameValuePair("B_LOCATION_1",param.getDep()),
					new NameValuePair("E_LOCATION_1",param.getArr()),
					new NameValuePair("TRIP_TYPE","R"),
					new NameValuePair("COMMERCIAL_FARE_FAMILY_1","ECOMOINS"),
					new NameValuePair("B_ANY_TIME_1","TRUE"),
					new NameValuePair("B_DATE_1",date1),
					new NameValuePair("DATE_ALLER",date2),
					new NameValuePair("DATE_RANGE_VALUE_1","0"),
					new NameValuePair("B_ANY_TIME_2","TRUE"),
					new NameValuePair("B_DATE_2",date3),
					new NameValuePair("DATE_RETOUR",date4),
					new NameValuePair("DATE_RANGE_VALUE_2","1"),
					new NameValuePair("FIELD_ADT_NUMBER","1"),
					new NameValuePair("FIELD_CHD_NUMBER","0"),
					new NameValuePair("FIELD_YCD_NUMBER","0"),
					new NameValuePair("FIELD_STU_NUMBER","0"),
					new NameValuePair("FIELD_INFANTS_NUMBER","0"),
					new NameValuePair("FIELD_YTH_NUMBER","0"),
			};
		    post.setRequestBody(names);
		 	post.setRequestHeader("Origin", "http://www.aircaraibes.com");
		 	post.setRequestHeader("Referer", "http://www.aircaraibes.com/?ERROR_LIST=66002");
			post.getParams().setContentCharset("UTF-8");

			httpClient.executeMethod(post);						
			return post.getResponseBodyAsString();
		} catch (Exception e) {			
			e.printStackTrace();
		} finally{
			if (post != null) {
				post.releaseConnection();
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
		if (html.contains("Nous ne sommes pas en mesure de trouver des recommandations")){
			result.setRet(true);
			result.setStatus(Constants.NO_RESULT);
			return result;	
		}		
		String jsonStr = StringUtils.substringBetween(html, "var generatedJSon = new String('", "');");
		try{
			JSONObject ajson = (JSONObject) JSON.parse(jsonStr);
			List<RoundTripFlightInfo> roundTripFlightList = new ArrayList<RoundTripFlightInfo>();//往返组合
			List<OneWayFlightInfo> outboundFlightList = new ArrayList<OneWayFlightInfo>();//去程列表
			List<OneWayFlightInfo> returnedFlightList = new ArrayList<OneWayFlightInfo>();//返程列表
			
//			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//			SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");
//			Date date = format.parse(param.getDepDate());
			JSONObject tabObj = ajson.getJSONObject("list_tab");
	        //匹配去程
	        genFlightList(outboundFlightList, tabObj.getJSONArray("list_proposed_bound").getJSONObject(0), param, param.getDepDate());
	        //匹配返程
	        genFlightList(returnedFlightList, tabObj.getJSONArray("list_proposed_bound").getJSONObject(1), param, param.getRetDate());
			
			//当去程或回程无数据时返回NO_RESULT
			if(outboundFlightList.size()==0||returnedFlightList.size()==0){
				result.setRet(true);
				result.setStatus(Constants.NO_RESULT);
				return result;
			}			
			
			JSONObject[][] priceArray = getPriceArray(outboundFlightList.size(),returnedFlightList.size(),  tabObj); 
//			for(int i = 0; i<flightList.size(); i++){
//				OneWayFlightInfo flight = flightList.get(i);
//				JSONObject priceObj = priceArray[i];
//				flight.getDetail().setPrice(Double.parseDouble(priceObj.getString("price")));
//				JSONObject priceInfo =  priceObj.getJSONArray("list_trip_price").getJSONObject(0);
//				flight.getDetail().setMonetaryunit(priceInfo.getJSONObject("currency").getString("code"));
//				flight.getDetail().setTax(Double.parseDouble(priceInfo.getString("tax_float")));
//			}
			//两层循环，对去程和返程list做笛卡尔积得到组合后的所有往返航程
			int i = 0;
			for(OneWayFlightInfo obfl:outboundFlightList){
				int j = 0;
				for(OneWayFlightInfo rtfl:returnedFlightList){
					RoundTripFlightInfo round = new RoundTripFlightInfo();
					round.setInfo(obfl.getInfo());//去程航段信息
					round.setOutboundPrice(obfl.getDetail().getPrice());//去程价格
					round.setReturnedPrice(rtfl.getDetail().getPrice());//返程价格
					FlightDetail detail =  cloneFlightDetail(obfl.getDetail());
					JSONObject priceObj = priceArray[i][j];
					JSONObject priceInfo =  priceObj.getJSONArray("list_trip_price").getJSONObject(0);
					detail.setPrice(Double.parseDouble(priceObj.getString("price")));//往返总价格
					detail.setMonetaryunit(priceInfo.getJSONObject("currency").getString("code"));
					detail.setTax(Double.parseDouble(priceInfo.getString("tax_float")));
					round.setDetail(detail);				//将设置后的去程信息装入往返中
					round.setRetdepdate(rtfl.getDetail().getDepdate());//返程日期
					round.setRetflightno(rtfl.getDetail().getFlightno());//返程航班号list
					round.setRetinfo(rtfl.getInfo());//返程信息
					roundTripFlightList.add(round);//添加到list
					j++;
				}
				i++;
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
		String bookingUrlPre = "https://vols.aircaraibes.com/plnext/aircaraibesB2C/Override.action";
		String date1 = param.getDepDate().replaceAll("-","")+"0000";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");
        
        Date retDate = null;
        Date date = null;
		try {
			date = format.parse(param.getDepDate());
			retDate=format.parse(param.getRetDate());
		} catch (ParseException e) {
			e.printStackTrace();
		}
        String date2 = format2.format(date);			
        String date3 = param.getRetDate().replaceAll("-","")+"0000";
        String date4 = format2.format(retDate);			
		
		
		BookingResult bookingResult = new BookingResult();
		
		BookingInfo bookingInfo = new BookingInfo();
		bookingInfo.setAction(bookingUrlPre);
		bookingInfo.setMethod("post");
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("SITE", "ACSQACSQ");
		map.put("EXTERNAL_ID","TEST");
		map.put("TRIP_FLOW","Yes");
		map.put("EMBEDDED_TRANSACTION","FlexPricerAvailability");
		map.put("LANGUAGE","FR");
		map.put("DIRECT_LOGIN","No");
		map.put("ARRANGE_BY","N");
		map.put("REFRESH","0");
		map.put("DATE_RANGE_QUALIFIER_1","C");
		map.put("DATE_RANGE_QUALIFIER_2","C");
		map.put("ENVIRONMENT","PRODUCTION");
		map.put("PRICING_TYPE","O");
		map.put("SO_SITE_CHARGEABLE_SEATMAP","TRUE");
		map.put("DISPLAY_TYPE","1");
		map.put("SO_SITE_ALLOW_SERVICE_FEE","1");
		map.put("SO_GL","<?xml version=\"1.0\" encoding=\"iso-8859-1\"?><SO_GL><GLOBAL_LIST mode=\"complete\"><NAME>SITE_SERVICE_FEE</NAME><LIST_ELEMENT><CODE>0</CODE><LIST_VALUE>0</LIST_VALUE><LIST_VALUE>2</LIST_VALUE><LIST_VALUE>5.00</LIST_VALUE><LIST_VALUE>EUR</LIST_VALUE></LIST_ELEMENT></GLOBAL_LIST></SO_GL>");
		map.put("TRAVELLER_TYPE_1","ADT");
		map.put("TRAVELLER_TYPE_2","");
		map.put("TRAVELLER_TYPE_3","");
		map.put("TRAVELLER_TYPE_4","");
		map.put("TRAVELLER_TYPE_5","");
		map.put("TRAVELLER_TYPE_6","");
		map.put("TRAVELLER_TYPE_7","");
		map.put("TRAVELLER_TYPE_8","");
		map.put("TRAVELLER_TYPE_9","");
		map.put("HAS_INFANT_1","");
		map.put("HAS_INFANT_2","");
		map.put("HAS_INFANT_3","");
		map.put("HAS_INFANT_4","");
		map.put("HAS_INFANT_5","");
		map.put("SO_LANG_URL_AIR_NFS_SRCH","http://www.aircaraibes.com/");
		map.put("SO_SITE_REDIRECT_MODE","AUTOMATIC");
		map.put("SO_SITE_MANUAL_ETKT_CMD","TTP/ITR-EMLA/INVJ-EMLA");
		map.put("SO_SITE_MOP_CALL_ME","FALSE");
		map.put("SO_SITE_ET_CODE_SHARE","00");
		map.put("SO_SITE_ALLOW_PROMO","FALSE");
		map.put("SO_SITE_MOP_EXTERNAL_INS","TRUE");
		map.put("DIRECT_NON_STOP","FALSE");
		map.put("SO_SITE_IS_INSURANCE_ENABLED","TRUE");
		map.put("SO_SITE_IS_INS_MERCHANT","FALSE");
		map.put("SO_SITE_MOP_EXTERNAL_INS","FALSE");
		map.put("SO_SITE_MINIMAL_TIME","H24");
		map.put("SO_SITE_NO_CC_ELLIGIBILITY","TRUE");
		map.put("SO_SITE_VERIF_INFANT_SSR","TRUE");
		map.put("SO_SITE_ALLOW_CS_CODE_SHARE","TRUE");
		map.put("SO_SITE_PROMPT_FEE","N");
		map.put("B_LOCATION_1",param.getDep());
		map.put("E_LOCATION_1",param.getArr());
		map.put("TRIP_TYPE","R");
		map.put("COMMERCIAL_FARE_FAMILY_1","ECOMOINS");
		map.put("B_ANY_TIME_1","TRUE");
		map.put("B_DATE_1",date1);
		map.put("DATE_ALLER",date2);
		map.put("DATE_RANGE_VALUE_1","0");
		map.put("B_ANY_TIME_2","TRUE");
		map.put("B_DATE_2",date3);
		map.put("DATE_RETOUR",date4);
		map.put("DATE_RANGE_VALUE_2","1");
		map.put("FIELD_ADT_NUMBER","1");
		map.put("FIELD_CHD_NUMBER","0");
		map.put("FIELD_YCD_NUMBER","0");
		map.put("FIELD_STU_NUMBER","0");
		map.put("FIELD_INFANTS_NUMBER","0");
		map.put("FIELD_YTH_NUMBER","0");
		bookingInfo.setInputs(map);		
		bookingResult.setData(bookingInfo);
		bookingResult.setRet(true);
		return bookingResult;
	}
	
	private void genFlightList(List<OneWayFlightInfo> flightList,JSONObject jsonObject, FlightSearchParam param, String datestr) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");
		try {
			Date date = format.parse(datestr);
			JSONArray flightArray = jsonObject.getJSONArray("list_flight");
			for (int i = 0; i < flightArray.size(); i++){
				JSONObject flightInfo = flightArray.getJSONObject(i);
				OneWayFlightInfo flight = new OneWayFlightInfo();
				FlightDetail flightDetail = new FlightDetail();
				List<FlightSegement> segs = new ArrayList<FlightSegement>();
				flightDetail.setWrapperid("gjsairtx001");
				flightDetail.setArrcity(param.getArr());
				flightDetail.setDepcity(param.getDep());
				flightDetail.setDepdate(date);
				List<String> flightNoList = new ArrayList<String>();
				flightDetail.setFlightno(flightNoList);
				flight.setDetail(flightDetail);
				flight.setInfo(segs);
				flightList.add(flight);
				JSONArray segArray =  flightInfo.getJSONArray("list_segment");
				for(int j=0; j< segArray.size(); j ++){
					JSONObject segObj = segArray.getJSONObject(j);
					FlightSegement seg = new FlightSegement();
					seg.setDeptime(segObj.getString("b_date_formatted_time"));
					seg.setDepairport(segObj.getJSONObject("b_location").getString("city_code"));
					seg.setArrtime(segObj.getString("e_date_formatted_time"));
					seg.setArrairport(segObj.getJSONObject("e_location").getString("city_code"));
					String flightNo = segObj.getJSONObject("airline").getString("code") + segObj.getString("flight_number");
					seg.setFlightno(flightNo);
					flightNoList.add(flightNo);
					String depDateStr = segObj.getString("b_date_date");
					Date depDate = format2.parse(depDateStr);
					seg.setDepDate(format.format(depDate));
					String arrDateStr = segObj.getString("e_date_date");
					Date arrDate = format2.parse(arrDateStr);
					seg.setArrDate(format.format(arrDate));
					segs.add(seg);
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	

	private JSONObject[][] getPriceArray(int deptSize, int rectSize, JSONObject boundJson) {
		JSONObject[][] priceArray = new JSONObject[deptSize][rectSize]; 
		JSONArray recArray = boundJson.getJSONArray("list_recommendation");
		for(int i=0; i< recArray.size(); i++){
			JSONObject recObj = recArray.getJSONObject(i);
			JSONArray  boundArray = recObj.getJSONArray("list_bound");
			JSONObject boundObj0 = boundArray.getJSONObject(0);
			JSONObject boundObj1 = boundArray.getJSONObject(1);
			JSONObject flight0= boundObj0.getJSONArray("list_flight").getJSONObject(0);
			JSONObject flight1= boundObj1.getJSONArray("list_flight").getJSONObject(0);
			int flightId0 = flight0.getIntValue("flight_id");
			int flightId1 = flight1.getIntValue("flight_id");
			if (priceArray[flightId0][flightId1] == null){
				priceArray[flightId0][flightId1] = recObj; 
			}
			else	if (priceArray[flightId0][flightId1].getFloatValue("price") > recObj.getFloatValue("price")){
				priceArray[flightId0][flightId1] = recObj;
			}
		}
		return priceArray;
	}
	
	private FlightDetail cloneFlightDetail(FlightDetail sDetail) {
		FlightDetail detail = new FlightDetail();
		detail.setArrcity(sDetail.getArrcity());
		detail.setCreatetime(sDetail.getCreatetime());
		detail.setDepcity(sDetail.getDepcity());
		detail.setDepdate(sDetail.getDepdate());
		detail.setFlightno(sDetail.getFlightno());
		detail.setMonetaryunit(sDetail.getMonetaryunit());
		detail.setPrice(sDetail.getPrice());
		detail.setSource(sDetail.getSource());
		detail.setStatus(sDetail.getStatus());
		detail.setTax(sDetail.getTax());
		detail.setUpdatetime(sDetail.getUpdatetime());
		detail.setWrapperid(sDetail.getWrapperid());
		return detail;
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
