package com.codextech.ibtisam.lepak_app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.posapi.PosApi;
import android.posapi.PrintQueue;
import android.posapi.PrintQueue.OnPrintListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codextech.ibtisam.lepak_app.R;
import com.codextech.ibtisam.lepak_app.SessionManager;
import com.codextech.ibtisam.lepak_app.model.Ticket;
import com.codextech.ibtisam.lepak_app.realm.RealmController;
import com.codextech.ibtisam.lepak_app.service.ScanService;
import com.codextech.ibtisam.lepak_app.sync.TicketSenderAsync;
import com.codextech.ibtisam.lepak_app.sync.MyUrls;
import com.codextech.ibtisam.lepak_app.util.DateAndTimeUtils;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;

public class TicketFormatActivity extends Activity {
    public static final String TAG = "TicketFormatActivity";
    public static final String CAR_NUMBER = "car_number";
    private Button btnPrintMix;
    private Bitmap mBitmap = null;
    private PrintQueue mPrintQueue = null;
    private byte mGpioPower = 0x1E;// PB14
    private int mCurSerialNo = 3; // usart3
    private int mBaudrate = 4; // 9600
    MediaPlayer player;
    boolean isCanPrint = true;
    TextView tvSiteName,
            tvtime,
            tvnumber,
            tvprice,
            tvlocation;
    private Realm realm;
    String ticket_time = "";
    String ticket_time_Out = "";
    String veh_number = "";
    String site_name = "";
    String veh_type = "car";
    String fee = "20";
    String device_location = "Lati/Logi";
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(TicketFormatActivity.this);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_ticket);
        this.realm = RealmController.with(this).getRealm();
        RealmController.with(this).refresh();
        Intent intenti = getIntent();
        veh_number = intenti.getStringExtra(TicketFormatActivity.CAR_NUMBER);
        site_name = sessionManager.getKeySiteName();
        long timeNowMillis = Calendar.getInstance().getTimeInMillis();
        ticket_time = DateAndTimeUtils.getDateTimeStringFromMiliseconds(timeNowMillis, "yyyy-MM-dd kk:mm:ss");
//        ticket_time = DateFormat.getDateTimeInstance().format(new Date());
        tvSiteName = (TextView) findViewById(R.id.tvSiteName);
        tvtime = (TextView) findViewById(R.id.Dtime);
        tvnumber = (TextView) findViewById(R.id.Dnumber);
        tvprice = (TextView) findViewById(R.id.Dprice);
        tvlocation = (TextView) findViewById(R.id.Dlocation);
        tvSiteName.setText(site_name);
        tvtime.setText(ticket_time);
        tvnumber.setText(veh_number);
        tvprice.setText(fee);
        tvlocation.setText(device_location);
        Toast.makeText(this, "  " + veh_number + " ", Toast.LENGTH_SHORT).show();

        btnPrintMix = (Button) this.findViewById(R.id.btnPrintMix);
        btnPrintMix.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                printMix();
//                saveTicket(site_name, ticket_time, ticket_time_Out, veh_number, veh_type, fee, device_location, "ticket_not_synced");
            }
        });
        mPrintQueue = new PrintQueue(this, ScanService.mApi);
        mPrintQueue.init();
        mPrintQueue.setOnPrintListener(new OnPrintListener() {
            @Override
            public void onFinish() {
                isCanPrint = true;
                syncTicket(sessionManager.getKeySiteId(), veh_number, veh_type, fee, ticket_time, sessionManager.getLoginToken());
                finish();
                Toast.makeText(getApplicationContext(), getString(R.string.print_complete), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(int state) {
                isCanPrint = true;
                switch (state) {
                    case PosApi.ERR_POS_PRINT_NO_PAPER:
                        showTip(getString(R.string.print_no_paper));
                        break;
                    case PosApi.ERR_POS_PRINT_FAILED:
                        showTip(getString(R.string.print_failed));
                        break;
                    case PosApi.ERR_POS_PRINT_VOLTAGE_LOW:
                        showTip(getString(R.string.print_voltate_low));
                        break;
                    case PosApi.ERR_POS_PRINT_VOLTAGE_HIGH:
                        showTip(getString(R.string.print_voltate_high));
                        break;
                }
            }

            @Override
            public void onGetState(int arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPrinterSetting(int state) {
                isCanPrint = true;
                switch (state) {
                    case 0:
                        Toast.makeText(TicketFormatActivity.this, "Continued with paper", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(TicketFormatActivity.this, "Out of paper", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(TicketFormatActivity.this, "Black mark is detected", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(PosApi.ACTION_POS_COMM_STATUS);
        registerReceiver(receiver, mFilter);
        player = MediaPlayer.create(getApplicationContext(), R.raw.beep);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equalsIgnoreCase(PosApi.ACTION_POS_COMM_STATUS)) {
                int cmdFlag = intent.getIntExtra(PosApi.KEY_CMD_FLAG, -1);
                int status = intent.getIntExtra(PosApi.KEY_CMD_STATUS, -1);
                int bufferLen = intent.getIntExtra(PosApi.KEY_CMD_DATA_LENGTH,
                        0);
                byte[] buffer = intent
                        .getByteArrayExtra(PosApi.KEY_CMD_DATA_BUFFER);

                switch (cmdFlag) {
                    case PosApi.POS_EXPAND_SERIAL_INIT:
                        if (status == PosApi.COMM_STATUS_SUCCESS) {
                            // ed_str.setText("open success\n ");
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                        } else {
                            // ed_str.setText("open fail\n");
                            Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case PosApi.POS_EXPAND_SERIAL3:
                        if (buffer == null)
                            return;

                        StringBuffer sb = new StringBuffer();
                        for (int i = 0; i < buffer.length; i++) {
                            if (buffer[i] == 0x0D) {
                                // sb.append("\n");
                            } else {
                                sb.append((char) buffer[i]);
                            }
                        }
                        player.start();
                        try {
                            String str = new String(buffer, "GBK");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                buffer = null;
            }
        }

    };

    private void openDevice() {
        // open power
        ScanService.mApi.gpioControl(mGpioPower, 0, 1);
        ScanService.mApi.extendSerialInit(mCurSerialNo, mBaudrate, 1, 1, 1, 1);
    }

    private void closeDevice() {
        // close power
        ScanService.mApi.gpioControl(mGpioPower, 0, 0);
        ScanService.mApi.extendSerialClose(mCurSerialNo);
    }

    private void addPrintTextWithSize(int size, int concentration, byte[] data) {
        if (data == null)
            return;
        byte[] _2x = new byte[]{0x1b, 0x57, 0x02};
        byte[] _1x = new byte[]{0x1b, 0x57, 0x01};
        byte[] mData = null;
        if (size == 1) {
            mData = new byte[3 + data.length];
            System.arraycopy(_1x, 0, mData, 0, _1x.length);
            System.arraycopy(data, 0, mData, _1x.length, data.length);
            mPrintQueue.addText(concentration, mData);
        } else if (size == 2) {
            mData = new byte[3 + data.length];
            System.arraycopy(_2x, 0, mData, 0, _2x.length);
            System.arraycopy(data, 0, mData, _2x.length, data.length);
            mPrintQueue.addText(concentration, mData);

        }
    }

    private void printMix() {
        try {
            int concentration = 44;
            StringBuilder sb = new StringBuilder();
            sb.append("   LEPARK Lahore Parking Company     ");
            sb.append("\n");
            sb.append("        PARKING TICKET     ");
            sb.append("\n");
            sb.append("Site Name: ");
            sb.append(site_name);
            sb.append("\n");
            sb.append("Time:  " + ticket_time);
            sb.append("\n");
            sb.append("Veh Reg No:  " + veh_number);
            sb.append("\n");
            sb.append("Veh Type:  " + veh_type);
            sb.append("\n");
            sb.append("Parking Fee:  20");
            sb.append("\n");
            sb.append("Location:  " + device_location);
            sb.append("\n");
            sb.append("--------------------------------");
            sb.append("   Parking at your own risk");
            sb.append("\n");
            sb.append("Parking company is not liable");
            sb.append("\n");
            sb.append("for any loss");
            sb.append("\n");
            byte[] text = null;
            text = sb.toString().getBytes("GBK");
            addPrintTextWithSize(1, concentration, text);
            sb = new StringBuilder();
            sb.append("\n");
            text = sb.toString().getBytes("GBK");
            addPrintTextWithSize(1, concentration, text);
            saveTicket(site_name, ticket_time, ticket_time_Out, veh_number, veh_type, fee, device_location, "ticket_not_synced");
            mPrintQueue.printStart();
            //TODO if ticket is printed successfull then do this
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void saveTicket(String site_name, String ticket_time_in, String ticket_time_out, String veh_number, String veh_type, String fee, String device_location, final String syncStatus) {

        if (site_name != null && ticket_time_in != null && veh_number != null && veh_type != null && fee != null && device_location != null) {

            Ticket ticket = new Ticket();
            ticket.setId(RealmController.getInstance().getTickets().size() + System.currentTimeMillis());
            ticket.setSiteName(site_name);
            ticket.setTimeIn(ticket_time_in);
            ticket.setTimeOut(ticket_time_out);
            ticket.setNumber(veh_number);
            ticket.setVehicleType(veh_type);
            ticket.setPrice(fee);
            ticket.setLocation(device_location);
            ticket.setSyncStatus(syncStatus);
            realm.beginTransaction();
            realm.copyToRealm(ticket);
            realm.commitTransaction();

            TicketSenderAsync ticketSenderAsync = new TicketSenderAsync(TicketFormatActivity.this);
            ticketSenderAsync.execute();


//            long count = realm.where(Ticket.class).count();
//            Log.d(TAG, "saveTicket: COUNT: " + count);
            // Toast.makeText(TicketFormatActivity.this, "Entry Saved" + RealmController.getInstance().getTickets().size() + System.currentTimeMillis(), Toast.LENGTH_SHORT).show();
        }
    }

    private void syncTicket(final String site_id, final String vehicle_no, final String vehicle_type, final String fee, final String ticket_time, final String token) {
        RequestQueue queue = Volley.newRequestQueue(TicketFormatActivity.this, new HurlStack());

        StringRequest postRequest = new StringRequest(Request.Method.POST, MyUrls.TICKET_SEND,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Toast.makeText(TicketFormatActivity.this, "Ticket Sent", Toast.LENGTH_SHORT).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        // error
                        Toast.makeText(TicketFormatActivity.this, "Error Sending Ticket", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();

                params.put("site_id", site_id);
                params.put("vehicle_no", vehicle_no);
                params.put("vehicle_type", vehicle_type);
                params.put("fee", fee);
                params.put("ticket_time", ticket_time);
                params.put("token", token);

                return params;
            }
        };
        queue.add(postRequest);

    }
    private void showTip(String msg) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.tips))
                .setMessage(msg)
                .setNegativeButton(getString(R.string.close),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        openDevice();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDevice();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBitmap != null) {
            mBitmap.recycle();
        }

        if (mPrintQueue != null) {
            mPrintQueue.close();
        }
        unregisterReceiver(receiver);
    }
}
