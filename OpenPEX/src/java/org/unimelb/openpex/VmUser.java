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

import java.io.Serializable;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 *
 * @author srikumar
 */
@Entity
@Table(name = "VM_USER")
@NamedQueries({@NamedQuery(name = "VmUser.findByUsername", query = "SELECT v FROM VmUser v WHERE v.username = :username"), @NamedQuery(name = "VmUser.findByUserid", query = "SELECT v FROM VmUser v WHERE v.userid = :userid"), @NamedQuery(name = "VmUser.findByPassword", query = "SELECT v FROM VmUser v WHERE v.password = :password"), @NamedQuery(name = "VmUser.findByFullname", query = "SELECT v FROM VmUser v WHERE v.fullname = :fullname"), @NamedQuery(name = "VmUser.findByProjid", query = "SELECT v FROM VmUser v WHERE v.projid = :projid"), @NamedQuery(name = "VmUser.findByEmail", query = "SELECT v FROM VmUser v WHERE v.email = :email"), @NamedQuery(name = "VmUser.findByBalance", query = "SELECT v FROM VmUser v WHERE v.balance = :balance")})
public class VmUser implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    @SequenceGenerator(name = "ID_GEN", allocationSize = 1, initialValue = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ID_GEN")
    @Column(name = "userid", nullable = false)
    private Short userid;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "fullname", nullable = false)
    private String fullname;
    @Column(name = "projid")
    private Short projid;
    @Column(name = "email", nullable = false)
    private String email;
    @Column(name = "balance", nullable = false)
    private float balance;

    public VmUser() {
    }

    public VmUser(Short userid) {
        this.userid = userid;
    }

    public VmUser(Short userid, String username, String password, String fullname, String email, float balance) {
        this(userid);
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.email = email;
        this.balance = balance;
    }

    public VmUser(String username, String password, String fullname, String email) {
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.email = email;
        this.balance = (float) 0.0;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Short getUserid() {
        return userid;
    }

    public void setUserid(Short userid) {
        this.userid = userid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Short getProjid() {
        return projid;
    }

    public void setProjid(Short projid) {
        this.projid = projid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userid != null ? userid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VmUser)) {
            return false;
        }
        VmUser other = (VmUser) object;
        if ((this.userid == null && other.userid != null) || (this.userid != null && !this.userid.equals(other.userid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.unimelb.pex.VmUser[userid=" + userid + "]";
    }
}
