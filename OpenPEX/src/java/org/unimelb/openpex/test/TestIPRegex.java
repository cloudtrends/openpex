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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.unimelb.pex.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author srikumar
 */
public class TestIPRegex {
    
    public static void main(String[] args){
        String input = "{0/ip=128.250.33.183}";
        String pattern = "(\\d{1,3}\\.){3}\\d{1,3}";
//        String pattern ="((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(input);
        if(m.find())
            System.out.println(input.substring(m.start(), m.end()));
        else 
            System.out.println("no match!");
      
    }

}
