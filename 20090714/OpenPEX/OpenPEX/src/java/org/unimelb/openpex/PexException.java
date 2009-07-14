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

package org.unimelb.openpex;


public class PexException extends Exception {
    /**
     * Constructor for PexException
     * @param msg - the error message
     * @param cause - the exception object which causes the error
     */
    public PexException(String msg,Throwable cause) {
        super(msg,cause);
    }

    /**
     * Constructor for GridBrokerException
     * @param msg - the error message
     */
    public PexException(String msg) {
        super(msg);
    }
}
