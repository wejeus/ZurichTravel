package co.daresay.bellatrix;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;
import java.util.List;

import co.daresay.bellatrix.capture2.CaptureActivity2;
import co.daresay.bellatrix.graph.Graph;
import co.daresay.bellatrix.graph.GraphData;
import co.daresay.bellatrix.graph.OnBarClickListener;
import co.daresay.bellatrix.io.Checkpoint;
import co.daresay.bellatrix.io.Connection;
import co.daresay.bellatrix.io.ConnectionsResponse;
import co.daresay.bellatrix.io.Journey;
import co.daresay.bellatrix.io.Section;
import co.daresay.bellatrix.io.TransportService;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends Activity {

    TransportService transportService;

    EditText inputFrom;
    EditText inputTo;

    Graph graph;

    View graph_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Travel");

        graph_container = findViewById(R.id.graph_container);
        inputFrom = findViewById(R.id.input_from);
        inputTo = findViewById(R.id.input_to);

        graph = findViewById(R.id.graph);
        setOnBarClickListener();

        ((EditText) findViewById(R.id.input_to)).setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        // Identifier of the action. This will be either the identifier you supplied,
                        // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                        if (actionId == EditorInfo.IME_ACTION_SEARCH
                                || actionId == EditorInfo.IME_ACTION_DONE
                                || event.getAction() == KeyEvent.ACTION_DOWN
                                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            search(inputFrom, inputTo);
                            return true;
                        }
                        // Return true if you have consumed the action, else false.
                        return false;
                    }
                });

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://transport.opendata.ch/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        transportService = retrofit.create(TransportService.class);

        final EditText inputFrom = (EditText) findViewById(R.id.input_from);
        final EditText inputTo = (EditText) findViewById(R.id.input_to);

        TextView buttonCheckin = findViewById(R.id.button_checkin);
        buttonCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(MainActivity.this, CaptureActivity.class);
//                startActivity(i);
                Intent i = new Intent(MainActivity.this, CaptureActivity2.class);
                startActivity(i);
            }
        });
    }

    private void setOnBarClickListener() {
        graph.onBarClickListener = new OnBarClickListener() {
            public void onBarClick(GraphData graphData, int position) {
                displayGraphData(graphData);
            }
        };
    }

    private void displayGraphData(GraphData graphData) {
        findViewById(R.id.selected_trip_details).setVisibility(View.VISIBLE);

        ImageView li = findViewById(R.id.details_left_image);
        li.setImageResource(graphData.leftImage);

        ImageView ri = findViewById(R.id.details_right_image);
        ri.setImageResource(graphData.rightImage);

        ImageView bi = findViewById(R.id.details_bottom_image);
        bi.setImageResource(graphData.botImage);
    }

    private void search(EditText inputFrom, EditText inputTo) {
        String from = inputFrom.getText().toString();
        String to = inputTo.getText().toString();
        findConnections(from, to);

        graph_container.setVisibility(View.VISIBLE);

        TextView buttonCheckin = findViewById(R.id.button_checkin);
        buttonCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CaptureActivity2.class);
                startActivity(i);
            }
        });

        initGraph(graph);
        int initialSelection = 7;
        graph.setSelectedItem(initialSelection, getGraphData().get(initialSelection));

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(inputFrom.getWindowToken(), 0);
    }

    public static void initGraph(Graph graph) {
        List<GraphData> data = getGraphData();

        graph.setData(data);
    }

    @android.support.annotation.NonNull
    private static List<GraphData> getGraphData() {
        List<GraphData> data = new ArrayList<>();

        float norm = 93;

        float[] values = {
                26,
                29,
                43,
                80,
                88,
                93,
                80,
                86,
                78,
                65,
                48,
                42,
                42,
                33,
                37,
                42,
                31,
        };

        int COUNT = 17;
        for (int i = 1; i < COUNT + 1; ++i) {
            GraphData graphData = new GraphData();
            float value = values[i - 1] / norm;
            graphData.value = value;
//            graphData.label = labels[i - 1];

            if (value >= 0.52f) {
                graphData.leftImage = R.drawable.travel2;
                graphData.rightImage = R.drawable.p2;
                graphData.botImage = R.drawable.text1;
            } else {
                graphData.leftImage = R.drawable.travel1;
                graphData.rightImage = R.drawable.p17;
                graphData.botImage = R.drawable.text2;
            }

            data.add(graphData);
        }
        return data;
    }

    private void findConnections(String from, String to) {
        final Observable<ConnectionsResponse> connections = transportService.findConnections(from, to);
        connections
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ConnectionsResponse>() {
                               @Override
                               public void accept(ConnectionsResponse connectionsResponse) throws Exception {
                                   for (int i = 0; i < connectionsResponse.connections.length; ++i) {
                                       Connection connection = connectionsResponse.connections[i];
                                       Log.d("MY", "CONNECTION from: " + connection.from.station.name + " to: " + connection.to.station.name);
                                       for (int j = 0; j < connection.sections.length; ++j) {
                                           Section section = connection.sections[j];
                                           Log.d("MY", "SECTION departure " + section.departure.station.name + " arrival " + section.arrival.station.name);
                                           Journey journey = section.journey;
                                           for (int k = 0; k < journey.passList.length; ++k) {
                                               Checkpoint journeyCheckpoint = journey.passList[k];
                                               Log.d("MY", "passList: " + journeyCheckpoint.station.name);
                                           }
                                       }
                                   }
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {
                                Log.e("error", "hi", throwable);
                            }
                        }
                );
    }

}
