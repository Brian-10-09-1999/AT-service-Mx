package com.example.devolucionmaterial.activitys.foliosPendientesSeccion;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;

import android.os.Bundle;

import java.util.Calendar;

/**
 * Created by EDGAR ARANA on 28/05/2018.
 */

public class DatePickerFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);

        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(),
                (DatePickerDialog.OnDateSetListener)
                        getActivity(), year, month, day);
    }
}
