package com.codextech.ibtisam.lepak_app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codextech.ibtisam.lepak_app.R;
import com.codextech.ibtisam.lepak_app.SessionManager;
import com.codextech.ibtisam.lepak_app.activity.TicketFormatActivity;
import com.codextech.ibtisam.lepak_app.model.LPTicket;
import com.codextech.ibtisam.lepak_app.realm.RealmController;

/**
 * Created by HP on 10/18/2017.
 */

public class PrintTicketFragment extends Fragment {
    public static final String TAG = "test";
    private Button bCar;
    private Button bBike;
    private Button bVan;
    private Button bTruck;
    private EditText edenternumber;
    private String vehNumber;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: PrintTicketFragment");
        View view = inflater.inflate(R.layout.print_ticket_fragment, container, false);
        edenternumber = (EditText) view.findViewById(R.id.enterNum);

        bCar = (Button) view.findViewById(R.id.bCar);
        bBike = (Button) view.findViewById(R.id.bBike);
        bVan = (Button) view.findViewById(R.id.bVan);
        bTruck = (Button) view.findViewById(R.id.bTruck);
        sessionManager = new SessionManager(getActivity());
//        bCar.setText("CAR :"+sessionManager.getKeyCarAmount());
//        bBike.setText("BIKE :"+sessionManager.getKeyBikeAmount());
//        bVan.setText("VAN :"+sessionManager.getKeyVanAmount());
//        bTruck.setText("TRUCK :"+sessionManager.getKeyTruckAmount());

        if (Integer.parseInt(sessionManager.getKeyCarAmount()) <= 0) {
            // bCar.setText("CAR :"+sessionManager.getKeyCarAmount());
            bCar.setVisibility(View.GONE);

        }
        if (Integer.parseInt(sessionManager.getKeyBikeAmount()) <= 0) {
            //  bBike.setText("BIKE :"+sessionManager.getKeyBikeAmount());
            bBike.setVisibility(View.GONE);
        }

        if (Integer.parseInt(sessionManager.getKeyVanAmount()) <= 0) {
            // bVan.setText("VAN :"+sessionManager.getKeyVanAmount());
            bVan.setVisibility(View.GONE);
        }

        if (Integer.parseInt(sessionManager.getKeyTruckAmount()) <= 0) {
            // bTruck.setText("TRUCK :"+sessionManager.getKeyTruckAmount());
            bTruck.setVisibility(View.GONE);
        }


        bCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vehNumber = edenternumber.getText().toString();
                if (vehNumber.trim().length() >= 1 && vehNumber != null&&vehNumber.trim().length() <= 5) {
                    LPTicket lpTicket = RealmController.with(getActivity()).getTicketFromNumber(vehNumber);
                    if (lpTicket == null) {
                        Intent intent = new Intent(getActivity(), TicketFormatActivity.class);
                        intent.putExtra(TicketFormatActivity.KEY_VEHICLE_NUMBER, vehNumber);
                        intent.putExtra(TicketFormatActivity.KEY_VEHICLE_TYPE, TicketFormatActivity.VEHICLE_TYPE_CAR);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "Vehi already exists", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    edenternumber.setError("Empty  or too long");
                }
            }
        });
        bBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vehNumber = edenternumber.getText().toString();
                if (vehNumber.trim().length() >= 1 && vehNumber != null&&vehNumber.trim().length() <= 5) {
                    LPTicket lpTicket = RealmController.with(getActivity()).getTicketFromNumber(vehNumber);
                    if (lpTicket == null) {
                        Intent intent = new Intent(getActivity(), TicketFormatActivity.class);
                        intent.putExtra(TicketFormatActivity.KEY_VEHICLE_NUMBER, vehNumber);
                        intent.putExtra(TicketFormatActivity.KEY_VEHICLE_TYPE, TicketFormatActivity.VEHICLE_TYPE_BIKE);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "Vehi already exists", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    edenternumber.setError("Empty or too long");
                }
            }
        });
        bVan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vehNumber = edenternumber.getText().toString();
                if (vehNumber.trim().length() >= 1 && vehNumber != null&&vehNumber.trim().length() <= 5) {
                    LPTicket lpTicket = RealmController.with(getActivity()).getTicketFromNumber(vehNumber);
                    if (lpTicket == null) {
                        Intent intent = new Intent(getActivity(), TicketFormatActivity.class);
                        intent.putExtra(TicketFormatActivity.KEY_VEHICLE_NUMBER, vehNumber);
                        intent.putExtra(TicketFormatActivity.KEY_VEHICLE_TYPE, TicketFormatActivity.VEHICLE_TYPE_VAN);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "Vehi already exists", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    edenternumber.setError("Empty or too long");
                }
            }
        });
        bTruck.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                vehNumber = edenternumber.getText().toString();
                if (vehNumber.trim().length() >= 1 && vehNumber != null&&vehNumber.trim().length() <= 5) {
                    LPTicket lpTicket = RealmController.with(getActivity()).getTicketFromNumber(vehNumber);
                    if (lpTicket == null) {
                        Intent intent = new Intent(getActivity(), TicketFormatActivity.class);
                        intent.putExtra(TicketFormatActivity.KEY_VEHICLE_NUMBER, vehNumber);
                        intent.putExtra(TicketFormatActivity.KEY_VEHICLE_TYPE, TicketFormatActivity.VEHICLE_TYPE_TRUCK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "Vehi already exists", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    edenternumber.setError("Empty or too long");
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        edenternumber.setText("");
    }
}


