package impl.org.jfxcore.validation;

import org.jfxcore.validation.ConstrainedValue;
import org.jfxcore.validation.ValidationListener;
import org.jfxcore.validation.WeakValidationListener;
import org.jfxcore.validation.property.ReadOnlyConstrainedProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.scene.Node;

@SuppressWarnings({"FieldCanBeLocal", "rawtypes", "unchecked"})
public final class NodeValidationInfo implements ValidationListener, InvalidationListener {

    private static final PseudoClass VALIDATING_PSEUDOCLASS = PseudoClass.getPseudoClass("validating");
    private static final PseudoClass INVALID_PSEUDOCLASS = PseudoClass.getPseudoClass("invalid");
    private static final PseudoClass VALID_PSEUDOCLASS = PseudoClass.getPseudoClass("valid");
    private static final PseudoClass USER_INVALID_PSEUDOCLASS = PseudoClass.getPseudoClass("user-invalid");
    private static final PseudoClass USER_VALID_PSEUDOCLASS = PseudoClass.getPseudoClass("user-valid");

    private final WeakValidationListener weakValidationListener = new WeakValidationListener(this);
    private final ObservableValue<Boolean> userModified;
    private final Node node;

    private ConstrainedValue<?, ?> source;

    public NodeValidationInfo(Node node) {
        this.node = node;
        this.userModified = ControlValidationSupport.tryGetUserModifiedProperty(node);
        if (this.userModified != null) {
            this.userModified.addListener(this);
        }

        node.pseudoClassStateChanged(VALIDATING_PSEUDOCLASS, false);
        node.pseudoClassStateChanged(VALID_PSEUDOCLASS, false);
        node.pseudoClassStateChanged(INVALID_PSEUDOCLASS, false);
        node.pseudoClassStateChanged(USER_VALID_PSEUDOCLASS, false);
        node.pseudoClassStateChanged(USER_INVALID_PSEUDOCLASS, false);
    }

    public void setSource(ConstrainedValue<?, ?> source) {
        boolean userModified = isUserModified();
        ConstrainedValue<?, ?> oldValue = this.source;
        ConstrainedValue<?, ?> newValue = source;

        if (oldValue != null) {
            oldValue.removeListener(weakValidationListener);
        }

        if (oldValue instanceof ReadOnlyConstrainedProperty<?,?> property) {
            PropertyHelper.getValidationHelper(property).updateUserModified(false);
        }

        if (newValue != null) {
            newValue.addListener(weakValidationListener);
        }

        if (newValue instanceof ReadOnlyConstrainedProperty<?,?> property) {
            PropertyHelper.getValidationHelper(property).updateUserModified(userModified);
        }

        node.pseudoClassStateChanged(VALID_PSEUDOCLASS, newValue != null && newValue.isValid());
        node.pseudoClassStateChanged(INVALID_PSEUDOCLASS, newValue != null && newValue.isInvalid());
        node.pseudoClassStateChanged(USER_VALID_PSEUDOCLASS, userModified && newValue != null && newValue.isValid());
        node.pseudoClassStateChanged(USER_INVALID_PSEUDOCLASS, userModified && newValue != null && newValue.isInvalid());
        node.pseudoClassStateChanged(VALIDATING_PSEUDOCLASS, newValue != null && newValue.isValidating());

        this.source = newValue;
    }

    public ConstrainedValue<?, ?> getSource() {
        return source;
    }

    /**
     * Called when the 'userModified' property was changed.
     */
    @Override
    public void invalidated(Observable observable) {
        boolean userModified = isUserModified();

        if (source instanceof ReadOnlyConstrainedProperty<?,?> property) {
            PropertyHelper.getValidationHelper(property).updateUserModified(userModified);
        }

        node.pseudoClassStateChanged(USER_VALID_PSEUDOCLASS, source != null && source.isValid() && userModified);
        node.pseudoClassStateChanged(USER_INVALID_PSEUDOCLASS, source != null && source.isInvalid() && userModified);
    }

    /**
     * Called when the validation state was changed.
     */
    @Override
    public void changed(ConstrainedValue value, ChangeType changeType, boolean oldValue, boolean newValue) {
        switch (changeType) {
            case VALID -> {
                node.pseudoClassStateChanged(VALID_PSEUDOCLASS, newValue);
                node.pseudoClassStateChanged(USER_VALID_PSEUDOCLASS, newValue && isUserModified());
            }

            case INVALID -> {
                node.pseudoClassStateChanged(INVALID_PSEUDOCLASS, newValue);
                node.pseudoClassStateChanged(USER_INVALID_PSEUDOCLASS, newValue && isUserModified());
            }

            case VALIDATING -> node.pseudoClassStateChanged(VALIDATING_PSEUDOCLASS, newValue);
        }
    }

    private boolean isUserModified() {
        return userModified != null && userModified.getValue();
    }

}
