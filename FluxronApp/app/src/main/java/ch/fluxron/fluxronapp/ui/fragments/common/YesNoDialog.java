package ch.fluxron.fluxronapp.ui.fragments.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Represents a dialog that has yes and no options for the user
 */
public class YesNoDialog extends DialogFragment {

    /**
     * Listens to yes or no from the YesNoDialog
     */
    public interface IYesNoListener {
        void yesSelected(int action);
        void noSelected(int action);
    }

    private IYesNoListener listener;

    /**
     * Sets the listener for this dialog instance
     * @param listener Listener
     */
    public void setListener(IYesNoListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Bundle args = getArguments();
        String title = args.getString("title", "");
        String message = args.getString("message", "");

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (listener!=null) listener.yesSelected(getTargetRequestCode());
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (listener!=null) listener.noSelected(getTargetRequestCode());
                    }
                })
                .create();
    }
}
