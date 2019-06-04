package com.esacinc.spd.model.complex_extensions;

import ca.uhn.fhir.model.api.IElement;
import ca.uhn.fhir.model.api.IExtension;
import ca.uhn.fhir.model.api.annotation.*;
import ca.uhn.fhir.model.primitive.IdDt;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.UriType;

import java.util.List;

public interface IEndpointUseCase {

    @Block
    class VhDirEndpointUseCase implements IExtension {

        /**
         * Add the Type
         */
        @Child(name = "caseType", type = {CodeableConcept.class})
        @Extension(url="type", definedLocally=false, isModifier=false)
        @Description(shortDefinition="Use case type")
        @Binding(valueSet="http://hl7.org/fhir/uv/vhdir/CodeSystem/codesystem-usecase")
        protected CodeableConcept type;

        /**
         * Add the standard
         */
        @Child(name = "standard", type = {UriType.class})
        @Extension(url="standard", definedLocally=false, isModifier=false)
        @Description(shortDefinition="Use case standard")
        protected UriType standard;

        public void setType(CodeableConcept type) {
            this.type = type;
        }

        public void setStandard(UriType standard) {
            this.standard = standard;
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
