//    “Copyright 2008, 2009 Srikumar Venugopal & James Broberg”
//
//    This file is part of OpenPEX.
//
//    OpenPEX is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 2 of the License, or
//    (at your option) any later version.
//
//    OpenPEX is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with OpenPEX.  If not, see <http://www.gnu.org/licenses/>.

package org.unimelb.openpex.rest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

/**
 *
 * @author brobergj
 */
public class JsonHTTPDateValueProcessor implements JsonValueProcessor {

    private static final String RFC_1123_DATE_FORMAT =
            "E, dd MMM yyyy HH:mm:ss z";
    private static final String RFC_1123_TIMEZONE = "GMT";
    private DateFormat dateFormat;

    public JsonHTTPDateValueProcessor() {
        this.setDateFormat(new SimpleDateFormat(RFC_1123_DATE_FORMAT));
        this.getDateFormat().setTimeZone(TimeZone.getTimeZone(RFC_1123_TIMEZONE));
    }

    /**
     * @return the dateFormat
     */
    public DateFormat getDateFormat() {
        return dateFormat;
    }

    /**
     * @param dateFormat the dateFormat to set
     */
    public void setDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    public Object processArrayValue(Object value, JsonConfig config) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object processObjectValue(String key, Object value, JsonConfig config) {

        System.err.println("Calling processObjectValue()");
        // If it's a Date convert to RFC_1123_DATE_FORMAT
        if (value instanceof Date) {
            String str = getDateFormat().format((Date) value);
            return str;
        }
        // If it's a Calendar convert to RFC_1123_DATE_FORMAT
        if (value instanceof Calendar) {
            String str = getDateFormat().format(((Calendar)value).getTime());
            return str;
        }
        // If it's a RFC_1123_DATE_FORMAT String convert to Date
        if (key.contains("Time")) {
            System.err.println("Converting string date to cal");
            Date date = null;
           
            try {
                date = getDateFormat().parse((String) value);

            } catch (ParseException ex) {
                Logger.getLogger(JsonHTTPDateValueProcessor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NullPointerException ex) {
                Logger.getLogger(JsonHTTPDateValueProcessor.class.getName()).log(Level.SEVERE, null, ex);
                return "";
            }
            return date;
        }


        return value == null ? null : value.toString();
    }
}
