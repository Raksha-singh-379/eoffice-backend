package com.techvg.eoffice.service.dto;

import com.techvg.eoffice.domain.enumeration.DakStatus;
import com.techvg.eoffice.domain.enumeration.LetterType;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.techvg.eoffice.domain.DakMaster} entity.
 */
public class DakMasterDTO implements Serializable {

    private Long id;

    private String inwardNumber;

    private String senderName;

    private String contactNumber;

    private String senderAddress;

    private String senderEmail;

    private String subject;

    private Instant letterDate;

    private DakStatus currentStatus;

    private Boolean letterStatus;

    private Instant letterReceivedDate;

    private String awaitReason;

    private Instant dispatchDate;

    private String createdBy;

    private LetterType letterType;

    private Boolean isResponseReceived;

    private Instant assignedDate;

    private Instant lastModified;

    private String lastModifiedBy;

    private String dakAssignedFrom;

    private String dakAssignee;

    private String dispatchBy;

    private String senderOutward;

    private String outwardNumber;

    private String taluka;

    private OrganizationDTO organization;

    private Set<SecurityUserDTO> securityUsers = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInwardNumber() {
        return inwardNumber;
    }

    public void setInwardNumber(String inwardNumber) {
        this.inwardNumber = inwardNumber;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Instant getLetterDate() {
        return letterDate;
    }

    public void setLetterDate(Instant letterDate) {
        this.letterDate = letterDate;
    }

    public DakStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(DakStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Boolean getLetterStatus() {
        return letterStatus;
    }

    public void setLetterStatus(Boolean letterStatus) {
        this.letterStatus = letterStatus;
    }

    public Instant getLetterReceivedDate() {
        return letterReceivedDate;
    }

    public void setLetterReceivedDate(Instant letterReceivedDate) {
        this.letterReceivedDate = letterReceivedDate;
    }

    public String getAwaitReason() {
        return awaitReason;
    }

    public void setAwaitReason(String awaitReason) {
        this.awaitReason = awaitReason;
    }

    public Instant getDispatchDate() {
        return dispatchDate;
    }

    public void setDispatchDate(Instant dispatchDate) {
        this.dispatchDate = dispatchDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LetterType getLetterType() {
        return letterType;
    }

    public void setLetterType(LetterType letterType) {
        this.letterType = letterType;
    }

    public Boolean getIsResponseReceived() {
        return isResponseReceived;
    }

    public void setIsResponseReceived(Boolean isResponseReceived) {
        this.isResponseReceived = isResponseReceived;
    }

    public Instant getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(Instant assignedDate) {
        this.assignedDate = assignedDate;
    }

    public Instant getLastModified() {
        return lastModified;
    }

    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getDakAssignedFrom() {
        return dakAssignedFrom;
    }

    public void setDakAssignedFrom(String dakAssignedFrom) {
        this.dakAssignedFrom = dakAssignedFrom;
    }

    public String getDakAssignee() {
        return dakAssignee;
    }

    public void setDakAssignee(String dakAssignee) {
        this.dakAssignee = dakAssignee;
    }

    public String getDispatchBy() {
        return dispatchBy;
    }

    public void setDispatchBy(String dispatchBy) {
        this.dispatchBy = dispatchBy;
    }

    public String getSenderOutward() {
        return senderOutward;
    }

    public void setSenderOutward(String senderOutward) {
        this.senderOutward = senderOutward;
    }

    public String getOutwardNumber() {
        return outwardNumber;
    }

    public void setOutwardNumber(String outwardNumber) {
        this.outwardNumber = outwardNumber;
    }

    public String getTaluka() {
        return taluka;
    }

    public void setTaluka(String taluka) {
        this.taluka = taluka;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public Set<SecurityUserDTO> getSecurityUsers() {
        return securityUsers;
    }

    public void setSecurityUsers(Set<SecurityUserDTO> securityUsers) {
        this.securityUsers = securityUsers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DakMasterDTO)) {
            return false;
        }

        DakMasterDTO dakMasterDTO = (DakMasterDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, dakMasterDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DakMasterDTO{" +
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
            ", organization=" + getOrganization() +
            ", securityUsers=" + getSecurityUsers() +
            "}";
    }
}
