package elcarmen.project.community.Business;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import elcarmen.project.community.Data.API_Access;
import elcarmen.project.community.Data.HttpGetBitmap;
import elcarmen.project.community.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommunityEventsFragment extends Fragment {

    TextView txtMesAno;
    Button btnMesAnterior, btnMesSiguiente;
    ListView lvEvents;
    FloatingActionButton ftbtnCreateEvent;
    int mesActual, anoActual;
    private String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

    ArrayList<Event> events = new ArrayList<Event>();
    ArrayList<Event> events_month = new ArrayList<Event>();
    ArrayList<String> comunidades = new ArrayList<String>();
    ArrayList<String> comunidades_mes = new ArrayList<String>();

    static ArrayAdapter arrayAdapter;

    public CommunityEventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_community_events, container, false);
        txtMesAno = view.findViewById(R.id.txtMesAno);
        btnMesAnterior = view.findViewById(R.id.btnMesAnterior);
        btnMesAnterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarMes(-1);
            }
        });
        btnMesSiguiente = view.findViewById(R.id.btnMesSiguiente);
        btnMesSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarMes(1);
            }
        });

        lvEvents = view.findViewById(R.id.lvEvents);
        if(User_Singleton.getInstance().isAdmin(CommunityActivity.idCommunity)){
            lvEvents.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                    final int event_mont_deleted = position;
                    //final int idEvent = events_month.get(position);

                    if(getActivity() instanceof CommunityActivity){
                        if(events_month.get(position).approved){
                            new AlertDialog.Builder(getContext())
                                    .setIcon(R.drawable.ic_event_delete)
                                    .setTitle("Está seguro?")
                                    .setMessage("Desea eliminar evento?")
                                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            ExecuteModifyEvents executeModifyEvents = new ExecuteModifyEvents(1, position);
                                            executeModifyEvents.execute();
                                        }
                                    })
                                    .setNegativeButton("No", null)
                                    .show();
                        }else{
                            new AlertDialog.Builder(getContext())
                                    .setIcon(R.drawable.ic_event_available_black_24dp)
                                    .setTitle("Aprobar?")
                                    .setMessage("Desea aprobar el evento?")
                                    .setPositiveButton("Aprobar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            ExecuteModifyEvents executeModifyEvents = new ExecuteModifyEvents(0, position);
                                            executeModifyEvents.execute();
                                        }
                                    })
                                    .setNegativeButton("Eliminar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            ExecuteModifyEvents executeModifyEvents = new ExecuteModifyEvents(1, position);
                                            executeModifyEvents.execute();
                                        }
                                    })
                                    .show();
                        }
                    }
                    return false;
                }
            });
        }


        ftbtnCreateEvent = view.findViewById(R.id.ftbtnCreateEvent);
        ftbtnCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateEventActivity.class);
                startActivity(intent);
            }
        });

        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        mesActual = cal.get(Calendar.MONTH);
        anoActual = cal.get(Calendar.YEAR);
        txtMesAno.setText(meses[mesActual] + " " + anoActual);


        if(getActivity() instanceof EventsActivity){
            int tipo = 1;
            ftbtnCreateEvent.setVisibility(View.INVISIBLE);
            ExecuteGetEvents executeGetEvents = new ExecuteGetEvents(tipo);
            executeGetEvents.execute();
        }
      
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser){
            int tipo = 0;
            if(getActivity() instanceof CommunityActivity) {
                tipo = 0;
            }else if(getActivity() instanceof EventsActivity){
                tipo = 1;
                ftbtnCreateEvent.setVisibility(View.INVISIBLE);
            }
            ExecuteGetEvents executeGetEvents = new ExecuteGetEvents(tipo);
            executeGetEvents.execute();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(getActivity() instanceof CommunityActivity) {
            int tipo = 0;

            ExecuteGetEvents executeGetEvents = new ExecuteGetEvents(tipo);
            executeGetEvents.execute();
        }

    }

    private void cambiarMes(int inc_dec){
        mesActual += inc_dec;

        if(mesActual < 0){
            anoActual--;
            mesActual = 11;
        }else if(mesActual > 11){
            anoActual++;
            mesActual = 0;
        }

        txtMesAno.setText(meses[mesActual] + " " + anoActual);
        setEventListMonth();
        lvEvents.setAdapter(new EventsAdapter());
    }

    private void setEventListMonth(){
        events_month.clear();
        comunidades_mes.clear();
        for(int i = 0; i < events.size(); i++){
            if(events.get(i).getYear() == anoActual && events.get(i).getMonth() == mesActual){
                events_month.add(events.get(i));
                if(getActivity() instanceof EventsActivity){
                    comunidades_mes.add(comunidades.get(i));
                }
            }
        }
    }

    public void cargarEventos(JSONObject jsonResult){
        try {
            events.clear();
            comunidades.clear();
            String token = jsonResult.getString("auth_token");
            User_Singleton.getInstance().setAuth_token(token);
            JSONArray jsonEventsList = jsonResult.getJSONArray("events");
            for (int i = 0; i < jsonEventsList.length(); i++) {
                JSONObject jsonEvent = jsonEventsList.getJSONObject(i);

                Event event = new Event(jsonEvent.getInt("id"), jsonEvent.getInt("id_community"), jsonEvent.getString("title"), jsonEvent.getString("description"), jsonEvent.getString("dateEvent"), jsonEvent.getString("start"), jsonEvent.getString("end"), jsonEvent.getString("photo"), jsonEvent.getBoolean("approved"));
                events.add(event);
            }

            JSONArray jsonCommunitiesNamesList = jsonResult.getJSONArray("comm_names");
            for (int i = 0; i < jsonEventsList.length(); i++) {
                comunidades.add(jsonCommunitiesNamesList.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setEventListMonth();
        lvEvents.setAdapter(new EventsAdapter());
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class EventsAdapter extends BaseAdapter {

        public EventsAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return events_month.size();
        }

        @Override
        public Object getItem(int i) {
            return events_month.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            if (view == null) {
                view = inflater.inflate(R.layout.event_list_item, null);
            }

            TextView txtFechaEvento = view.findViewById(R.id.txtFechaEvento);
            TextView txtCommName = view.findViewById(R.id.txtCommName);
            TextView txtEventName = view.findViewById(R.id.txtEventName);
            TextView txtEventHours = view.findViewById(R.id.txtEventHours);

            txtFechaEvento.setText(events_month.get(i).getFecha());
            if(getActivity() instanceof  CommunityActivity){
                txtCommName.setText(CommunityActivity.nameCommunity);
            }else{
                txtCommName.setText(comunidades_mes.get(i));
            }

            txtEventName.setText(events_month.get(i).getTitle());
            txtEventHours.setText(events_month.get(i).getHours());

            if (!(events_month.get(i).isApproved())) {
                view.setBackgroundColor(getResources().getColor(R.color.colorAccentTransparent));
            }

            return view;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class ExecuteGetEvents extends AsyncTask<String, Void, String> {
        boolean isOk = false;
        int tipo = 0; // 0: eventos de comunidad; 1: eventos de todas las comunidades del usuario

        public ExecuteGetEvents(int tipo) {
            this.tipo = tipo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            API_Access api = API_Access.getInstance();
            User_Singleton user = User_Singleton.getInstance();
            if(tipo == 0){
                String[] keys = {"id", "auth_token", "id_community"};
                String[] values = {Integer.toString(user.getId()), user.getAuth_token(), Integer.toString(CommunityActivity.idCommunity)};
                isOk = api.get_delete_base(keys, values, 17, "GET",1);
            }else{
                String[] keys = {"id", "auth_token"};
                String[] values = {Integer.toString(user.getId()), user.getAuth_token()};
                isOk = api.get_delete_base(keys, values, 18, "GET",1);
            }



            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                cargarEventos(API_Access.getInstance().getJsonObjectResponse());
            }else{
                String mensaje = "Error al obtener las eventos";

                Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class ExecuteModifyEvents extends AsyncTask<String, Void, String> {
        boolean isOk = false;
        int tipo = 0; // 0: aprobar; 1: eliminar
        int position; // posicion en events_month

        public ExecuteModifyEvents(int tipo, int position) {
            this.tipo = tipo;
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            API_Access api = API_Access.getInstance();
            User_Singleton user = User_Singleton.getInstance();
            String[] keys = {"id", "auth_token", "id_event"};
            String[] values = {Integer.toString(user.getId()), user.getAuth_token(), Integer.toString(events_month.get(position).getId())};

            if(tipo == 0){
                isOk = api.post_put_base(keys, values, 26, "PUT",1);
            }else{
                isOk = api.get_delete_base(keys, values, 27, "DELETE",1);
            }



            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                if(tipo == 1){
                    events.remove(events_month.get(position));
                    events_month.remove(position);
                }else{
                    JSONObject response = API_Access.getInstance().getJsonObjectResponse();

                    User_Singleton user = User_Singleton.getInstance();
                    try {
                        user.setAuth_token(response.getString("auth_token"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    events_month.get(position).setApproved(true);
                    for(int i = 0; i < events.size(); i++){
                        if(events.get(i).getId() == events_month.get(position).getId()){
                            events.get(i).setApproved(true);
                        }
                    }
                }

                lvEvents.setAdapter(new EventsAdapter());
            }else{
                String mensaje = "Error al modificar el evento";

                Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
