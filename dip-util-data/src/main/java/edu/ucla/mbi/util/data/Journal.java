package edu.ucla.mbi.util.data;

/* =========================================================================
 * $HeadURL::                                                              $
 * $Id::                                                                   $
 * Version: $Rev::                                                         $
 *==========================================================================
 *
 * DataSource - a source of DataItem(s)
 *             
 *
 ======================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 

public class DataSource{

    Set<Organization> organizations = new TreeSet<Organization>();
    Set<Publication> publications = new TreeSet<Publication>();
    Set<Person> curators = new TreeSet<Person>();

    DataSource() {
    }

    void setPaperAbstract(String paperAbstract) {
        this.paperAbstract = paperAbstract;
    }

    public Set<Person> getCurators() {
        return curators;
    }

    public void setCurators(Set<Person> curators) {
        this.curators = curators;
    }

    Set<Publication> getPublications() {
        return publications;
    }

    void setPublications(Set<Publication> publications) {
        this.publications = publications;
    }


    Set<Organization> getOrganizations() {
        return organizations;
    }

    void setOrganizations(Set<Organization> organizations) {
        this.organizations = organizations;
    }


    void setName(String name) {
        this.name = name;
    }

    void setComment(String comment) {
        this.comment = comment;
    }

    void setWebsiteURL(String websiteURL) {
        this.websiteURL = websiteURL;
    }

    private Integer id;

    Integer getId() {
        return id;
    }

    void setId(Integer id) {
        this.id = id;
    }

    public String getUniqueSearchKey() {
        return getName();
    }

//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        final Journal journal = (Journal) o;
//
//        return getName().equals(journal.getName());
//
//    }

}
