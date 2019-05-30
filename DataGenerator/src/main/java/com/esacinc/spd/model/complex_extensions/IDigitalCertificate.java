package com.esacinc.spd.model.complex_extensions;

import ca.uhn.fhir.model.api.IElement;
import ca.uhn.fhir.model.api.IExtension;
import ca.uhn.fhir.model.api.annotation.Block;
import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.Extension;
import ca.uhn.fhir.model.primitive.IdDt;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.StringType;

import java.util.Date;
import java.util.List;

public interface IDigitalCertificate {

    @Block
    class VhDirDigitalCertificate implements IExtension {

        /**
         * Add the type
         */
        @Child(name="type", type = {Coding.class}, order=1, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
        @Extension(url = "type", definedLocally = true, isModifier = false)
        @Description(shortDefinition="Type")
        private Coding type;

        /**
         * Add the use
         */
        @Child(name="use", type = {Coding.class}, order=2, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
        @Extension(url = "use", definedLocally = true, isModifier = false)
        @Description(shortDefinition="Use")
        private Coding use;

        /**
         * Add the standard
         */
        @Child(name="certificateStandard", type = {Coding.class}, order=3, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
        @Extension(url = "certificateStandard", definedLocally = true, isModifier = false)
        @Description(shortDefinition="certificateStandard")
        private Coding certificateStandard;

        /**
         * Add the cert
         */
        @Child(name="certificate", type = {StringType.class}, order=4, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
        @Extension(url = "certificate",
                definedLocally = true,
                isModifier = false)
        @Description(shortDefinition="Certificate")
        private StringType certificate;

        /**
         * Add the expiration date
         */
        @Child(name="expirationDate", type = {DateType.class}, order=5, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
        @Extension(url = "expirationDate",
                definedLocally = true,
                isModifier = false)
        @Description(shortDefinition="Expiration Date")
        private DateType expirationDate;

        /**
         * Add the expiration date
         */
        @Child(name="trustFramework", type = {CodeableConcept.class}, order=6, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
        @Extension(url = "trustFramework",
                definedLocally = true,
                isModifier = false)
        @Description(shortDefinition="trustFramework Date")
        private CodeableConcept trustFramework;

        public void setType(Coding type) {
            this.type = type;
        }

        public void setUse(Coding use) {
            this.use = use;
        }

        public void setCertificateStandard(Coding certificateStandard) {
            this.certificateStandard = certificateStandard;
        }

        public void setCertificate(String certificate) {
            this.certificate = new StringType(certificate);
        }

        public void setExpirationDate(Date expirationDate) {
            this.expirationDate = new DateType(expirationDate);
        }

        public void setTrustFramework(CodeableConcept trustFramework) {
            this.trustFramework = trustFramework;
        }

        @Override
        public <T extends IElement> List<T> getAllPopulatedChildElementsOfType(Class<T> aClass) {
            return null;
        }

        @Override
        public String getElementSpecificId() {
            return null;
        }

        @Override
        public IdDt getId() {
            return null;
        }

        @Override
        public void setElementSpecificId(String s) {

        }

        @Override
        public void setId(IdDt idDt) {

        }

        @Override
        public void setId(String s) {

        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean hasFormatComment() {
            return false;
        }

        @Override
        public List<String> getFormatCommentsPre() {
            return null;
        }

        @Override
        public List<String> getFormatCommentsPost() {
            return null;
        }
    }
}
