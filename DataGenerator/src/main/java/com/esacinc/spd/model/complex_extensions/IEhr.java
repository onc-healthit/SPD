package com.esacinc.spd.model.complex_extensions;

import ca.uhn.fhir.model.api.IElement;
import ca.uhn.fhir.model.api.IExtension;
import ca.uhn.fhir.model.api.annotation.Block;
import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.primitive.IdDt;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.StringType;

import java.util.ArrayList;
import java.util.List;

public interface IEhr {

    @Block
    class VhDirEhr implements IExtension {

        /**
         * Developer
         */
        @Child(name = "developer", min=1, max=1, modifier=false, summary=true)
        @Description(shortDefinition="Developer", formalDefinition="Developer" )
        protected StringType developer;

        /**
         * Product
         */
        @Child(name = "product", min=1, max=1, modifier=false, summary=true)
        @Description(shortDefinition="Product", formalDefinition="Product" )
        protected StringType product;

        /**
         * Version
         */
        @Child(name = "version", min=1, max=1, modifier=false, summary=true)
        @Description(shortDefinition="Version", formalDefinition="Version" )
        protected StringType version;

        /**
         * Certification Edition
         */
        @Child(name = "certificationEdition", min=1, max=1, modifier=false, summary=true)
        @Description(shortDefinition="Certification Edition", formalDefinition="Certification Edition" )
        protected Coding certificationEdition;

        /**
         * Patient Access
         */
        @Child(name = "patientAccess", min=1, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
        @Description(shortDefinition="Certification Edition", formalDefinition="Certification Edition" )
        protected List<CodeableConcept> patientAccess;

        /**
         * Certification ID
         */
        @Child(name = "certificationID", min=1, max=1, modifier=false, summary=true)
        @Description(shortDefinition="Certification ID", formalDefinition="Certification ID" )
        protected StringType certificationID;

        public void setDeveloper(StringType developer) {
            this.developer = developer;
        }

        public void setProduct(StringType product) {
            this.product = product;
        }

        public void setVersion(StringType version) {
            this.version = version;
        }

        public void setCertificationEdition(Coding certificationEdition) {
            this.certificationEdition = certificationEdition;
        }

        public void setPatientAccess(List<CodeableConcept> patientAccess) {
            this.patientAccess = patientAccess;
        }

        public void addPatientAccess(CodeableConcept t) {
            if (this.patientAccess == null)
                this.patientAccess = new ArrayList<>();
            this.patientAccess.add(t);
        }

        public void setCertificationID(StringType certificationID) {
            this.certificationID = certificationID;
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
