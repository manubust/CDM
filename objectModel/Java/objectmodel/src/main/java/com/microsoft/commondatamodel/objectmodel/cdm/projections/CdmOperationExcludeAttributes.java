// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License. See License.txt in the project root for license information.

package com.microsoft.commondatamodel.objectmodel.cdm.projections;

import com.microsoft.commondatamodel.objectmodel.cdm.CdmAttributeContext;
import com.microsoft.commondatamodel.objectmodel.cdm.CdmCorpusContext;
import com.microsoft.commondatamodel.objectmodel.cdm.CdmObject;
import com.microsoft.commondatamodel.objectmodel.cdm.CdmObjectBase;
import com.microsoft.commondatamodel.objectmodel.enums.CdmAttributeContextType;
import com.microsoft.commondatamodel.objectmodel.enums.CdmObjectType;
import com.microsoft.commondatamodel.objectmodel.enums.CdmOperationType;
import com.microsoft.commondatamodel.objectmodel.resolvedmodel.projections.ProjectionAttributeState;
import com.microsoft.commondatamodel.objectmodel.resolvedmodel.projections.ProjectionAttributeStateSet;
import com.microsoft.commondatamodel.objectmodel.resolvedmodel.projections.ProjectionContext;
import com.microsoft.commondatamodel.objectmodel.resolvedmodel.projections.ProjectionResolutionCommonUtil;
import com.microsoft.commondatamodel.objectmodel.utilities.*;
import com.microsoft.commondatamodel.objectmodel.utilities.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Class to handle ExcludeAttributes operations
 */
public class CdmOperationExcludeAttributes extends CdmOperationBase {
    private String TAG = CdmOperationExcludeAttributes.class.getSimpleName();
    private List<String> excludeAttributes;

    public CdmOperationExcludeAttributes(final CdmCorpusContext ctx) {
        super(ctx);
        this.setObjectType(CdmObjectType.OperationExcludeAttributesDef);
        this.setType(CdmOperationType.ExcludeAttributes);
        this.excludeAttributes = new ArrayList<>();
    }

    @Override
    public CdmObject copy(ResolveOptions resOpt, CdmObject host) {
        Logger.error(TAG, this.getCtx(), "Projection operation not implemented yet.", "copy");
        return new CdmOperationExcludeAttributes(this.getCtx());
    }

    public List<String> getExcludeAttributes() {
        return excludeAttributes;
    }

    public void setExcludeAttributes(final List<String> excludeAttributes) {
        this.excludeAttributes = excludeAttributes;
    }

    /**
     * @deprecated This function is extremely likely to be removed in the public interface, and not
     * meant to be called externally at all. Please refrain from using it.
     */
    @Override
    @Deprecated
    public Object copyData(final ResolveOptions resOpt, final CopyOptions options) {
        return CdmObjectBase.copyData(this, resOpt, options, CdmOperationExcludeAttributes.class);
    }

    @Override
    public String getName() {
        return "operationExcludeAttributes";
    }

    /**
     * @deprecated This function is extremely likely to be removed in the public interface, and not
     * meant to be called externally at all. Please refrain from using it.
     */
    @Override
    @Deprecated
    public CdmObjectType getObjectType() {
        return CdmObjectType.OperationExcludeAttributesDef;
    }

    @Override
    public boolean isDerivedFrom(String baseDef, ResolveOptions resOpt) {
        Logger.error(TAG, this.getCtx(), "Projection operation not implemented yet.", "isDerivedFrom");
        return false;
    }

    @Override
    public boolean validate() {
        ArrayList<String> missingFields = new ArrayList<>();

        if (this.excludeAttributes == null) {
            missingFields.add("excludeAttributes");
        }
        if (missingFields.size() > 0) {
            Logger.error(TAG, this.getCtx(), Errors.validateErrorString(this.getAtCorpusPath(), missingFields));
            return false;
        }
        return true;
    }

    @Override
    public boolean visit(final String pathFrom, final VisitCallback preChildren, final VisitCallback postChildren) {
        String path = "";
        if (!this.getCtx().getCorpus().getBlockDeclaredPathChanges()) {
            path = this.getDeclaredPath();
            if (StringUtils.isNullOrEmpty(path)) {
                path = pathFrom + "operationExcludeAttributes";
                this.setDeclaredPath(path);
            }
        }

        if (preChildren != null && preChildren.invoke(this, path)) {
            return false;
        }

        if (postChildren != null && postChildren.invoke(this, path)) {
            return true;
        }

        return false;
    }

    /**
     * @deprecated This function is extremely likely to be removed in the public interface, and not
     * meant to be called externally at all. Please refrain from using it.
     */
    @Override
    @Deprecated
    public ProjectionAttributeStateSet appendProjectionAttributeState(ProjectionContext projCtx, ProjectionAttributeStateSet projOutputSet, CdmAttributeContext attrCtx) {
        // Create a new attribute context for the operation
        AttributeContextParameters attrCtxOpExcludeAttrsParam = new AttributeContextParameters();
        attrCtxOpExcludeAttrsParam.setUnder(attrCtx);
        attrCtxOpExcludeAttrsParam.setType(CdmAttributeContextType.OperationExcludeAttributes);
        attrCtxOpExcludeAttrsParam.setName("operation/index" + this.getIndex() + "/operationExcludeAttributes");

        CdmAttributeContext attrCtxOpExcludeAttrs = CdmAttributeContext.createChildUnder(projCtx.getProjectionDirective().getResOpt(), attrCtxOpExcludeAttrsParam);

        // Get the top-level attribute names of the attributes to exclude
        // We use the top-level names because the exclude list may contain a previous name our current resolved attributes had
        Map<String, String> topLevelExcludeAttributeNames = ProjectionResolutionCommonUtil.getTopList(projCtx, this.excludeAttributes);

        // Iterate through all the projection attribute states generated from the source's resolved attributes
        // Each projection attribute state contains a resolved attribute that it is corresponding to
        for (ProjectionAttributeState currentPAS : projCtx.getCurrentAttributeStateSet().getValues()) {
            // Check if the current projection attribute state's resolved attribute is in the list of attributes to exclude
            // If this attribute is not in the exclude list, then we are including it in the output
            if (!topLevelExcludeAttributeNames.containsKey(currentPAS.getCurrentResolvedAttribute().getResolvedName())) {
                // Create a new attribute context for the attribute that we are including
                AttributeContextParameters attrCtxAddedAttrParam = new AttributeContextParameters();
                attrCtxAddedAttrParam.setUnder(attrCtx);
                attrCtxAddedAttrParam.setType(CdmAttributeContextType.AttributeDefinition);
                attrCtxAddedAttrParam.setName(currentPAS.getCurrentResolvedAttribute().getResolvedName());

                CdmAttributeContext attrCtxAddedAttr = CdmAttributeContext.createChildUnder(projCtx.getProjectionDirective().getResOpt(), attrCtxAddedAttrParam);

                // Create a projection attribute state for the included attribute
                // We only create projection attribute states for attributes that are not in the exclude list
                // Add the current projection attribute state as the previous state of the new projection attribute state
                ProjectionAttributeState newPAS = new ProjectionAttributeState(projOutputSet.getCtx());
                newPAS.setCurrentResolvedAttribute(currentPAS.getCurrentResolvedAttribute());
                newPAS.setPreviousStateList(new ArrayList<>(Arrays.asList(currentPAS)));

                projOutputSet.add(newPAS);
            } else {
                // The current projection attribute state's resolved attribute is in the exclude list

                // Get the attribute name the way it appears in the exclude list
                // For our attribute context, we want to use the attribute name the attribute has in the exclude list rather than its current name
                String excludeAttributeName = topLevelExcludeAttributeNames.get(currentPAS.getCurrentResolvedAttribute().getResolvedName());

                // Create a new attribute context for the excluded attribute
                AttributeContextParameters attrCtxExcludedAttrParam = new AttributeContextParameters();
                attrCtxExcludedAttrParam.setUnder(attrCtxOpExcludeAttrs);
                attrCtxExcludedAttrParam.setType(CdmAttributeContextType.AttributeDefinition);
                attrCtxExcludedAttrParam.setName(excludeAttributeName);

                CdmAttributeContext attrCtxExcludedAttr = CdmAttributeContext.createChildUnder(projCtx.getProjectionDirective().getResOpt(), attrCtxExcludedAttrParam);
            }
        }

        return projOutputSet;
    }
}