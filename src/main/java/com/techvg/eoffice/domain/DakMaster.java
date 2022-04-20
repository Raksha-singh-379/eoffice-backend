package com.techvg.eoffice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.techvg.eoffice.domain.enumeration.DakStatus;
import com.techvg.eoffice.domain.enumeration.LetterType;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A DakMaster.
 */
@Entity
@Table(name = "dak_master")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DakMaster implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "inward_number")
    private String inwardNumber;

    @Column(name = "sender_name")
    private String senderName;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "sender_address")
    private String senderAddress;

    @Column(name = "sender_email")
    private String senderEmail;

    @Column(name = "subject")
    private String subject;

    @Column(name = "letter_date")
    private Instant letterDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status")
    private DakStatus currentStatus;

    @Column(name = "letter_status")
    private Boolean letterStatus;

    @Column(name = "letter_received_date")
    private Instant letterReceivedDate;

    @Column(name = "await_reason")
    private String awaitReason;

    @Column(name = "dispatch_date")
    private Instant dispatchDate;

    @Column(name = "created_by")
    private String createdBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "letter_type")
    private LetterType letterType;

    @Column(name = "is_response_received")
    private Boolean isResponseReceived;

    @Column(name = "assigned_date")
    private Instant assignedDate;

    @Column(name = "last_modified")
    private Instant lastModified;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "dak_assigned_from")
    private String dakAssignedFrom;

    @Column(name = "dak_assignee")
    private String dakAssignee;

    @Column(name = "dispatch_by")
    private String dispatchBy;

    @Column(name = "sender_outward")
    private String senderOutward;

    @Column(name = "outward_number")
    private String outwardNumber;

    @Column(name = "taluka")
    private String taluka;

    @ManyToOne
    private Organization organization;

    @ManyToMany
    @JoinTable(
        name = "rel_dak_master__security_user",
        joinColumns = @JoinColumn(name = "dak_master_id"),
        inverseJoinColumns = @JoinColumn(name = "security_user_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "organization", "securityPermissions", "securityRoles", "dakMasters" }, allowSetters = true)
    private Set<SecurityUser> securityUsers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DakMaster id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInwardNumber() {
        return this.inwardNumber;
    }

    public DakMaster inwardNumber(String inwardNumber) {
        this.setInwardNumber(inwardNumber);
        return this;
    }

    public void setInwardNumber(String inwardNumber) {
        this.inwardNumber = inwardNumber;
    }

    public String getSenderName() {
        return this.senderName;
    }

    public DakMaster senderName(String senderName) {
        this.setSenderName(senderName);
        return this;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getContactNumber() {
        return this.contactNumber;
    }

    public DakMaster contactNumber(String contactNumber) {
        this.setContactNumber(contactNumber);
        return this;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getSenderAddress() {
        return this.senderAddress;
    }

    public DakMaster senderAddress(String senderAddress) {
        this.setSenderAddress(senderAddress);
        return this;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getSenderEmail() {
        return this.senderEmail;
    }

    public DakMaster senderEmail(String senderEmail) {
        this.setSenderEmail(senderEmail);
        return this;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getSubject() {
        return this.subject;
    }

    public DakMaster subject(String subject) {
        this.setSubject(subject);
        return this;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Instant getLetterDate() {
        return this.letterDate;
    }

    public DakMaster letterDate(Instant letterDate) {
        this.setLetterDate(letterDate);
        return this;
    }

    public void setLetterDate(Instant letterDate) {
        this.letterDate = letterDate;
    }

    public DakStatus getCurrentStatus() {
        return this.currentStatus;
    }

    public DakMaster currentStatus(DakStatus currentStatus) {
        this.setCurrentStatus(currentStatus);
        return this;
    }

    public void setCurrentStatus(DakStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Boolean getLetterStatus() {
        return this.letterStatus;
    }

    public DakMaster letterStatus(Boolean letterStatus) {
        this.setLetterStatus(letterStatus);
        return this;
    }

    public void setLetterStatus(Boolean letterStatus) {
        this.letterStatus = letterStatus;
    }

    public Instant getLetterReceivedDate() {
        return this.letterReceivedDate;
    }

    public DakMaster letterReceivedDate(Instant letterReceivedDate) {
        this.setLetterReceivedDate(letterReceivedDate);
        return this;
    }

    public void setLetterReceivedDate(Instant letterReceivedDate) {
        this.letterReceivedDate = letterReceivedDate;
    }

    public String getAwaitReason() {
        return this.awaitReason;
    }

    public DakMaster awaitReason(String awaitReason) {
        this.setAwaitReason(awaitReason);
        return this;
    }

    public void setAwaitReason(String awaitReason) {
        this.awaitReason = awaitReason;
    }

    public Instant getDispatchDate() {
        return this.dispatchDate;
    }

    public DakMaster dispatchDate(Instant dispatchDate) {
        this.setDispatchDate(dispatchDate);
        return this;
    }

    public void setDispatchDate(Instant dispatchDate) {
        this.dispatchDate = dispatchDate;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public DakMaster createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LetterType getLetterType() {
        return this.letterType;
    }

    public DakMaster letterType(LetterType letterType) {
        this.setLetterType(letterType);
        return this;
    }

    public void setLetterType(LetterType letterType) {
        this.letterType = letterType;
    }

    public Boolean getIsResponseReceived() {
        return this.isResponseReceived;
    }

    public DakMaster isResponseReceived(Boolean isResponseReceived) {
        this.setIsResponseReceived(isResponseReceived);
        return this;
    }

    public void setIsResponseReceived(Boolean isResponseReceived) {
        this.isResponseReceived = isResponseReceived;
    }

    public Instant getAssignedDate() {
        return this.assignedDate;
    }

    public DakMaster assignedDate(Instant assignedDate) {
        this.setAssignedDate(assignedDate);
        return this;
    }

    public void setAssignedDate(Instant assignedDate) {
        this.assignedDate = assignedDate;
    }

    public Instant getLastModified() {
        return this.lastModified;
    }

    public DakMaster lastModified(Instant lastModified) {
        this.setLastModified(lastModified);
        return this;
    }

    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
    }

    public String getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    public DakMaster lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getDakAssignedFrom() {
        return this.dakAssignedFrom;
    }

    public DakMaster dakAssignedFrom(String dakAssignedFrom) {
        this.setDakAssignedFrom(dakAssignedFrom);
        return this;
    }

    public void setDakAssignedFrom(String dakAssignedFrom) {
        this.dakAssignedFrom = dakAssignedFrom;
    }

    public String getDakAssignee() {
        return this.dakAssignee;
    }

    public DakMaster dakAssignee(String dakAssignee) {
        this.setDakAssignee(dakAssignee);
        return this;
    }

    public void setDakAssignee(String dakAssignee) {
        this.dakAssignee = dakAssignee;
    }

    public String getDispatchBy() {
        return this.dispatchBy;
    }

    public DakMaster dispatchBy(String dispatchBy) {
        this.setDispatchBy(dispatchBy);
        return this;
    }

    public void setDispatchBy(String dispatchBy) {
        this.dispatchBy = dispatchBy;
    }

    public String getSenderOutward() {
        return this.senderOutward;
    }

    public DakMaster senderOutward(String senderOutward) {
        this.setSenderOutward(senderOutward);
        return this;
    }

    public void setSenderOutward(String senderOutward) {
        this.senderOutward = senderOutward;
    }

    public String getOutwardNumber() {
        return this.outwardNumber;
    }

    public DakMaster outwardNumber(String outwardNumber) {
        this.setOutwardNumber(outwardNumber);
        return this;
    }

    public void setOutwardNumber(String outwardNumber) {
        this.outwardNumber = outwardNumber;
    }

    public String getTaluka() {
        return this.taluka;
    }

    public DakMaster taluka(String taluka) {
        this.setTaluka(taluka);
        return this;
    }

    public void setTaluka(String taluka) {
        this.taluka = taluka;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public DakMaster organization(Organization organization) {
        this.setOrganization(organization);
        return this;
    }

    public Set<SecurityUser> getSecurityUsers() {
        return this.securityUsers;
    }

    public void setSecurityUsers(Set<SecurityUser> securityUsers) {
        this.securityUsers = securityUsers;
    }

    public DakMaster securityUsers(Set<SecurityUser> securityUsers) {
        this.setSecurityUsers(securityUsers);
        return this;
    }

    public DakMaster addSecurityUser(SecurityUser securityUser) {
        this.securityUsers.add(securityUser);
        securityUser.getDakMasters().add(this);
        return this;
    }

    public DakMaster removeSecurityUser(SecurityUser securityUser) {
        this.securityUsers.remove(securityUser);
        securityUser.getDakMasters().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DakMaster)) {
            return false;
        }
        return id != null && id.equals(((DakMaster) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DakMaster{" +
            "id=" + getId() +
            ", inwardNumber='" + getInwardNumber() + "'" +
            ", senderName='" + getSenderName() + "'" +
            ", contactNumber='" + getContactNumber() + "'" +
            ", senderAddress='" + getSenderAddress() + "'" +
            ", senderEmail='" + getSenderEmail() + "'" +
            ", subject='" + getSubject() + "'" +
            ", letterDate='" + getLetterDate() + "'" +
            ", currentStatus='" + getCurrentStatus() + "'" +
            ", letterStatus='" + getLetterStatus() + "'" +
            ", letterReceivedDate='" + getLetterReceivedDate() + "'" +
            ", awaitReason='" + getAwaitReason() + "'" +
            ", dispatchDate='" + getDispatchDate() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", letterType='" + getLetterType() + "'" +
            ", isResponseReceived='" + getIsResponseReceived() + "'" +
            ", assignedDate='" + getAssignedDate() + "'" +
            ", lastModified='" + getLastModified() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", dakAssignedFrom='" + getDakAssignedFrom() + "'" +
            ", dakAssignee='" + getDakAssignee() + "'" +
            ", dispatchBy='" + getDispatchBy() + "'" +
            ", senderOutward='" + getSenderOutward() + "'" +
            ", outwardNumber='" + getOutwardNumber() + "'" +
            ", taluka='" + getTaluka() + "'" +
            "}";
    }
}
