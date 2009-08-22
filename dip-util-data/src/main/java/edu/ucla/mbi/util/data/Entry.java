package edu.ucla.mbi.util.data;

/* =========================================================================
 * $HeadURL::                                                              $
 * $Id::                                                                   $
 * Version: $Rev::                                                         $
 *==========================================================================
 *
 * DataItem - a traceable unit of data
 *
 *
 ======================================================================== */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DataItem {


    Set<IMExId> imexIds = new TreeSet<IMExId>();
    Set<PublicationComment> publicationComments = new TreeSet<PublicationComment>();

    Publication() {
    }

    void setDoi(PubId doi) {
        this.doi = doi;
    }
    void setImex(PubId imex)
    {
        this.imex = imex;
    }
    void setJournalSpecific(PubId journalSpecific)
    {
        this.journalSpecific = journalSpecific;
    }
    void setTitle(String title) {
        this.title = title;
    }

    void setAuthor(String author)
    {
       this.author = author; 
    }

    void setPubmed(PubId pubmedId) {
        this.pubmed = pubmedId;
    }

    void setJournal(Journal journal) {
        this.journal = journal;
    }

    void setCurator(Person curator) {
        this.curator = curator;
    }

    void setExpectedPublicationDate(GregorianCalendar expectedPublicationDate) {
        this.expectedPublicationDate = expectedPublicationDate;
    }

    void setReleaseDate(GregorianCalendar releaseDate) {
        this.releaseDate = releaseDate;
    }

    void setPublicationDate(GregorianCalendar publicationDate) {
        this.publicationDate = publicationDate;
    }

    void setResponsiblePerson(AbstractPerson responsiblePerson)
    {
       this.responsiblePerson =  responsiblePerson;
    }

    

    public Collection<Long> getIMExIds() {
        Set<Long> ids = new TreeSet<Long>();
        for (IMExId id : imexIds)
            ids.add(id.getImexId());
        return ids;
    }

    void setPaperAbstract(String paperAbstract) {
        this.paperAbstract = paperAbstract;
    }

    Set<PublicationComment> getPublicationComments() {
        return publicationComments;
    }

    void setPublicationComments(Set<PublicationComment> publicationComments) {
        this.publicationComments = publicationComments;
    }

    void setPublicationPublicationStatus(PublicationPublicationStatus publicationPublicationStatus) {

        this.publicationPublicationStatus = publicationPublicationStatus;

    }

    void setPublicationCurationStatus(PublicationCurationStatus publicationCurationStatus) {

        this.publicationCurationStatus = publicationCurationStatus;

    }

    Set<IMExId> getImexIds() {
        return imexIds;
    }

    void setImexIds(Set<IMExId> imexIds) {
        this.imexIds = imexIds;
    }

    void setPublicationIdentifier(PublicationIdentifier identifier) throws ObjectCreationException {
        if (identifier.getType().equals(PublicationIdentifier.Type.DOI))
            setDoi((PubId) identifier);
        else if (identifier.getType().equals(PublicationIdentifier.Type.PUBMED))
            setPubmed((PubId) identifier);
        else
            throw new ObjectCreationException("Cannot create a publication identifier of type \"" + identifier.getType() + "\"");
    }

    public String getUniqueSearchKey() {
        return getPublicationIdentifier().getIdentifier();
    }

    private Long id;

    private Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

}
