package org.atlas.gateway.utils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

import org.atlas.wsn.devices.Datatype;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DataUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(DataUtils.class);
	
	private static HashMap<String, String> datatypesMap = new HashMap<String, String>();
	private static ObjectMapper jsonMapper = new ObjectMapper();
	static{
		File sensServicesExist = new File(OsUtils.getEnviromentalVariable("GATEWAY_HOME")+"/config/datatypes.json");
		if( sensServicesExist.exists() ){
			try {
				Datatype[] datatypes = jsonMapper.readValue(sensServicesExist, Datatype[].class);
				for( Datatype dtt : datatypes ){
					System.out.println(dtt.getHandle() + " -- " +dtt.getType());
					datatypesMap.put(dtt.getHandle(),dtt.getType());
				}
			} catch (IOException e) {
				logger.error("Unable to parse datatypes",e);
			}
		}
	}
	
	public static String getDatatype(String handle){
		return datatypesMap.get(handle);
	}
	
	/**
	 * Convert to appropriate Hex Value, for the sampling Rate
	 * 	10 * value ms -> Add the hex value to the configuration. 
	 */
	public static String rateToHex(int rate, String manufacturer){
		int sampleOfTen = rate / 10;
		String val = Integer.toHexString(sampleOfTen).toUpperCase();
		if( val.length() == 1 ){
			val = "0"+val;
		}
		return val;
	}
	
	/**
	 *	Get a HEX value and return it's numeric 
	 * @param datatype - E.g light
	 * @param value - The Hex value to transform
	 * @param manufacturer - E.g TI
	 * @return transformed array.
	 */
	public static double transformDataToNumeric(String datatype, String value, String manufacturer){
		BigDecimal data = null;
		switch( datatype ){
			case "humidity":
				return new BigDecimal(calculateHumidity(value)).setScale(2, RoundingMode.HALF_UP).doubleValue();
			case "barometer":
				return new BigDecimal(calculatePressure(value)).setScale(2, RoundingMode.HALF_UP).doubleValue();
			case "light":
				return new BigDecimal(calculateLight(value)).setScale(2, RoundingMode.HALF_UP).doubleValue();				
			case "temperature":
				return new BigDecimal(calculateTemperature(value)).setScale(2, RoundingMode.HALF_UP).doubleValue();
		}
		return 0;
	}
	
	/*
     * Calculate light
     */
    private static double calculateLight(String value) {
        byte[] valueByte = hexStringToByteArray(value.replace(" ", ""));
        int sfloat = shortUnsignedAtOffset(valueByte, 0);
        int mantissa;
        int exponent;
        mantissa = sfloat & 0x0FFF;
        exponent = (sfloat & 0xF000) >> 12;
        return mantissa * (0.01 * Math.pow(2.0, exponent));
    }
    
    /*
     * Calculate temperature
     */
    private static double calculateTemperature(String value) {
        double[] temperatures = new double[2];
        byte[] valueByte = hexStringToByteArray(value.replace(" ", ""));
      	int ambT = shortUnsignedAtOffset(valueByte, 2);
      	int objT = shortUnsignedAtOffset(valueByte, 0);
        temperatures[0] = (ambT >> 2) * 0.03125;
        temperatures[1] = (objT >> 2) * 0.03125;
      	return temperatures[0];
    }
    
    /*
     * Calculate Humidity
     */
    private static double calculateHumidity(String value) {
        byte[] valueByte = hexStringToByteArray(value.replace(" ", ""));
        int hum = shortUnsignedAtOffset(valueByte, 2);
        double humf = 0f;
        humf = hum / 65536f * 100f;
        return humf;
    }
    
    /*
     * Calculate pressure
     */
    private static double calculatePressure(String value) {
        double p_a = 0.0;
        byte[] valueByte = hexStringToByteArray(value.replace(" ", ""));
        if (valueByte.length > 4) {
            Integer val = twentyFourBitUnsignedAtOffset(valueByte, 3);
            p_a = val / 100.0;
        } else {
            int mantissa;
            int exponent;
            Integer pre = shortUnsignedAtOffset(valueByte, 2);

            mantissa = pre & 0x0FFF;
            exponent = pre >> 12 & 0xFF;

            double output;
            double magnitude = Math.pow(2.0, exponent);
            output = mantissa * magnitude;
            p_a = output / 100.0;
        }
        return p_a;
    }
	
    // ---------------------------------------------------------------------------------------------
    //
    // Auxiliary methods
    //
    // ---------------------------------------------------------------------------------------------
    private static int unsignedToSigned(int unsigned, int bitLength) {
        if ((unsigned & 1 << bitLength - 1) != 0) {
            unsigned = -1 * ((1 << bitLength - 1) - (unsigned & (1 << bitLength - 1) - 1));
        }
        return unsigned;
    }

    private  static String hexAsciiToString(String hex) {
        hex = hex.replaceAll(" ", "");
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            String str = hex.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private static Integer shortSignedAtOffset(byte[] c, int offset) {
        Integer lowerByte = c[offset] & 0xFF;
        Integer upperByte = (int) c[offset + 1]; // Interpret MSB as signedan
        return (upperByte << 8) + lowerByte;
    }

    private static Integer shortUnsignedAtOffset(byte[] c, int offset) {
        Integer lowerByte = c[offset] & 0xFF;
        Integer upperByte = c[offset + 1] & 0xFF;
        return (upperByte << 8) + lowerByte;
    }

    private static Integer twentyFourBitUnsignedAtOffset(byte[] c, int offset) {
        Integer lowerByte = c[offset] & 0xFF;
        Integer mediumByte = c[offset + 1] & 0xFF;
        Integer upperByte = c[offset + 2] & 0xFF;
        return (upperByte << 16) + (mediumByte << 8) + lowerByte;
    }

}
