package sa.directtoweb.components.buttons;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.eocontrol.EODataSource;
import com.webobjects.eocontrol.EODetailDataSource;
import com.webobjects.eocontrol.EOEnterpriseObject;

import er.extensions.eof.ERXEOAccessUtilities;
import er.extensions.foundation.ERXValueUtilities;

public class SADRemoveRelatedButton extends SADDeleteButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public interface Keys extends SADActionButton.Keys {

		public static final String removeButtonLabel = "removeButtonLabel";
		public static final String cancelButtonLabel = "cancelButtonLabel";
		public static final String deleteButtonLabel = "deleteButtonLabel";
		public static final String classForRemoveObjButton = "classForRemoveObjButton";
		public static final String classForDisabledRemoveObjButton = "classForDisabledRemoveObjButton";
		public static final String confirmDeleteConfigurationName = "confirmDeleteConfigurationName";
		public static final String confirmDeleteOrRemoveRelatedMessage = "confirmDeleteOrRemoveRelatedMessage";
		public static final String confirmRemoveRelatedMessage = "confirmRemoveRelatedMessage";
		public static final String confirmDeleteRelatedMessage = "confirmDeleteRelatedMessage";
		public static final String classForRemoveDialogButton = "classForRemoveDialogButton";
		
	}
	
	private Boolean _showDeleteButton;
	private Boolean _showRemoveButton;
	private String _deleteButtonLabel;
	private String _removeButtonClass;
	private String _removeButtonLabel;
	
	public SADRemoveRelatedButton(WOContext context) {
        super(context);
    }

	/**
	 * Delete action for component button
	 * 
	 * Calls through to deleteObjectWithFinalCommit(false). Since this component is expected
	 * to be used in an edit form, the final commit will be handled buy the user save.
	 * 
	 */
	@Override
	public WOActionResults deleteAction() {
    		return deleteObjectWithFinalCommit(false);
	}

    /**
     * Removes the current object from the relationship
     */
    public WOActionResults removeAction() {
	    	WOActionResults result = null;
	    	dataSource().deleteObject(object());
	    	postDeleteNotification();
	    	return result;
    }
	
    // LABELS
    
    /**
     * Label for the Remove button
     * <p>
     * Defaults to "Remove"
     */
    @Override
    public String buttonLabel() {
    	if (_buttonLabel == null) {
			_buttonLabel = stringValueForBinding(Keys.removeButtonLabel, "Remove");
		}
		return _buttonLabel;
    }
    
    /**
     * CSS class for the Remove button.
     */
	@Override
	public String buttonClass() {
		String result = null;
		if (  hasAnyAction() && !showDialog() ) {
			result = activeButtonClass();
		} else {
			result = disabledButtonClass();
		}
		return result;
	}
	
	/**
	 * CSS class for an active Remove button
	 * <p>
     * Defaults to "Button ObjButton DeleteObjButton"
     * 
	 */
	@Override
	public String activeButtonClass() {
		if (_buttonClass == null) {
			_buttonClass = stringValueForBinding(Keys.classForRemoveObjButton, "btn btn-danger btn-xs");
		}
		return _buttonClass;
	}
	
	/**
	 * Css class for a disabled Remove button
	 * <p>
     * Defaults to "Button ObjButton DisabledObjButton DisabledDeleteObjButton"
     * 
	 */
	@Override
	public String disabledButtonClass() {
		if (_disabledButtonClass == null) {
			_disabledButtonClass = stringValueForBinding(Keys.classForDisabledRemoveObjButton, "btn btn-danger disabled btn-xs");
		}
		return _disabledButtonClass;
	}
	
    /**
     * Label for the dialog's delete button
     * <p>
     * Defaults to "Delete"
     */
    public String deleteButtonLabel() {
    	if (_deleteButtonLabel == null) {
			_deleteButtonLabel = stringValueForBinding(Keys.deleteButtonLabel, "Delete");
		}
		return _deleteButtonLabel;
    }

	/**
	 * Label for the dialog's remove button
	 * <p>
         * Defaults to "Remove"
	 */
	public String removeButtonLabel() {
		if (_removeButtonLabel == null) {
			_removeButtonLabel = stringValueForBinding(Keys.removeButtonLabel, "Remove");
		}
		return _removeButtonLabel;
	}

	/**
	 * @return the removeButtonClass
	 */
	public String removeButtonClass() {
		if (_removeButtonClass == null) {
			_removeButtonClass = stringValueForBinding(Keys.classForRemoveDialogButton, "btn btn-danger");
		}
		return _removeButtonClass;
	}
	
    /**
     * Boolean used to hide/show the confirmation dialog's remove button. 
     * 
     * The remove button show only be displayed if the reverse relationship for the related eo is not
     * mandatory and isEntityRemoveable returns true.
     */
	public Boolean showRemoveButton() {

		if(_showRemoveButton == null) {
		
			boolean isRemoveable = ERXValueUtilities.booleanValueWithDefault(d2wContext().valueForKey("isEntityRemoveable"), false);
			EODataSource ds = dataSource();
			
			if(ds != null && ds instanceof EODetailDataSource) {
			
				EODetailDataSource dds = (EODetailDataSource) ds;
				EOEnterpriseObject masterObj = (EOEnterpriseObject) dds.masterObject();
				EOEntity masterEntity = ERXEOAccessUtilities.entityForEo(masterObj);
				EORelationship relationship = masterEntity.relationshipNamed(dds.detailKey());
				EORelationship reverseRelationship = relationship.inverseRelationship();
			
				if(isRemoveable) {
					if(reverseRelationship == null) {
						_showRemoveButton = Boolean.TRUE;
					} else {
						_showRemoveButton = ! reverseRelationship.isMandatory();
					}
				} else {
					_showRemoveButton = Boolean.FALSE;
				}

			} else {
				_showRemoveButton = Boolean.valueOf(isRemoveable);
			}
		}
		
		return _showRemoveButton;
	
	}
	
    /**
     * Boolean used to hide/show the confirmation dialog's delete button
     * 
     * The delete button is only shown if isEntityDeletable returns true
     */
    public Boolean showDeleteButton() {

    		if (_showDeleteButton == null) {
			_showDeleteButton = Boolean.valueOf(canDelete() && ERXValueUtilities.booleanValue(valueForBinding("isEntityDeletable")));
		}
		
    		return _showDeleteButton;

    }
	
    public String dialogMessage() {
    	
    		if(_dialogMessage == null) {

    			Object result = null;
    			
    			if(showDeleteButton().booleanValue() && showRemoveButton().booleanValue()) {
        			result = d2wContextValueForBinding(Keys.confirmDeleteOrRemoveRelatedMessage);
    			} else if(showRemoveButton().booleanValue()) {
        			result = d2wContextValueForBinding(Keys.confirmRemoveRelatedMessage);
    			} else if(showDeleteButton().booleanValue()) {
        			result = d2wContextValueForBinding(Keys.confirmDeleteRelatedMessage);
    			} else {
        			result = "No actions available for this item";
    			}
    			
    			_dialogMessage = (String) result;

    		}
    		
    		return _dialogMessage;
    	
    }

    public boolean hasAnyAction() {
    		return showDeleteButton().booleanValue() || showRemoveButton().booleanValue();
    }
	
	
}