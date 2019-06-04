package com.esacinc.spd.model.complex_extensions;

import ca.uhn.fhir.model.api.IElement;
import ca.uhn.fhir.model.api.IExtension;
import ca.uhn.fhir.model.api.annotation.Block;
import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.Extension;
import ca.uhn.fhir.model.primitive.IdDt;
import org.hl7.fhir.r4.model.*;

import java.util.ArrayList;
import java.util.List;

public interface IQualification {

    @Block
    class VhDirQualification implements IExtension {

        @Child(name = "identifier", type = {Identifier.class}, min=0, max=Child.MAX_UNLIMITED)
        @Extension(url = "identifier", definedLocally = false, isModifier = false)
        @Description(shortDefinition="Identifiers")
        private List<Identifier> identifier;

        @Child(name = "code", type = {CodeableConcept.class}, min=1, max=1)
        @Extension(url = "code", definedLocally = false, isModifier = false)
        @Description(shortDefinition="Code")
        private CodeableConcept code;

        @Child(name = "issuer", type = {Reference.class}, min=1, max=1)
        @Extension(url = "issuer", definedLocally = false, isModifier = false)
        @Description(shortDefinition="Issuer")
        private Reference issuer;

        @Child(name = "status", type = {Coding.class}, min=0, max=1)
        @Extension(url = "status", definedLocally = false, isModifier = false)
        @Description(shortDefinition="Status")
        private Coding status;

        @Child(name = "period", type = {Period.class}, min=0, max=1)
        @Extension(url = "period", definedLocally = false, isModifier = false)
        @Description(shortDefinition="Period")
        private Period period;

        @Child(name = "whereValid", type = {Reference.class}, min=0, max=Child.MAX_UNLIMITED)
        @Extension(url = "whereValid", definedLocally = false, isModifier = false)
        @Description(shortDefinition="whereValid")
        private List<Reference> whereValid;

        public void setIdentifier(List<Identifier> val) {
            this.identifier = val;
        }

        public void addIdentifier(Identifier t) {
            if (this.identifier == null)
                this.identifier = new ArrayList<>();
            this.identifier.add(t);
        }

        public void setCode(CodeableConcept code) {
            this.code = code;
        }

        public void setIssuer(Reference issuer) {
            this.issuer = issuer;
        }

        public void setStatus(Coding status) {
            this.status = status;
        }

        public void setPeriod(Period period) {
            this.period = period;
        }

        public void setWhereValid(List<Reference> whereValid) {
            this.whereValid = whereValid;
        }

        public void addWhereValid(Reference t) {
            if (this.whereValid == null)
                this.whereValid = new ArrayList<>();
            this.whereValid.add(t);
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
