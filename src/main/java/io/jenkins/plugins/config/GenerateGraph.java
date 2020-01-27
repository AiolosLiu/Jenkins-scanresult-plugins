package io.jenkins.plugins.config;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class GenerateGraph {
    public String transformat(String root) {

        String dir = root + "/scan/testdata.json";
        String path = root + "/history";
        Object obj;
        JSONObject scanResult = new JSONObject();
        int high = 0, medium = 0, low = 0;
        LinkedHashMap<String, JSONArray> categories = new LinkedHashMap<>();
        try {
            JSONObject result = new JSONObject();
            obj = new JSONParser().parse(new FileReader(dir));
            JSONObject jo = (JSONObject) obj;
            JSONArray ja = (JSONArray) jo.get("vulnerabilties");
            // JSONObject jo2 = (JSONObject)ja.get(0);
            // System.out.println(jo2.get("score"));

            for (JSONObject vulnjson : (Iterable<JSONObject>) ja) {
                int risk = Integer.parseInt(vulnjson.get("score").toString());
                String type = "";
                if (risk < 4) {
                    ++low;
                    type = "low";
                } else if (risk < 7) {
                    ++medium;
                    type = "medium";
                } else {
                    ++high;
                    type = "high";
                }

                String ca = vulnjson.get("category").toString();
                if (!categories.containsKey(ca)) {
                    categories.put(ca, new JSONArray());
                }
                LinkedHashMap<String, String> m = new LinkedHashMap<>(3);
                m.put("title", vulnjson.get("title").toString());
                m.put("type", type);
                m.put("description", vulnjson.get("description").toString());

                categories.get(ca).add(m);
            }
            result.put("high", high);
            result.put("medium", medium);
            result.put("low", low);

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String title = path + "/record_" + timestamp.getTime();

            PrintWriter pw = new PrintWriter(title);
            pw.write(result.toJSONString());

            pw.flush();
            pw.close();

            scanResult.put("Categoris", categories);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scanResult.toJSONString();
    }
    //static String HISTORY_DATA_PATH = "./data/history";
    public DefaultCategoryDataset createDataset(String path) {

        String high = "high";
        String mid = "medium";
        String low = "low";

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        LinkedHashMap<Long, JSONObject> sortedMap = iterateRecords(path);
        sortedMap.forEach((timestamp, jsonData) -> {
            Date date = new Date(timestamp);
            Format format = new SimpleDateFormat("MM/dd/yyyy-HH:mm:ss");
            String time = format.format(date);
            dataset.addValue(Integer.parseInt(jsonData.get(high).toString()), high, time);
            dataset.addValue(Integer.parseInt(jsonData.get(mid).toString()), mid, time);
            dataset.addValue(Integer.parseInt(jsonData.get(low).toString()), low, time);
        });

        return dataset;
    }

    private LinkedHashMap<Long, JSONObject> iterateRecords(String path) {
        LinkedHashMap<Long, JSONObject> sortedMap = null;
        try {
            File dir = new File(path);
            File[] files = dir.listFiles();
            HashMap<Long, JSONObject> hm = new HashMap<>();
            if (files != null) {
                for (File file : files) {
                    String fileName = file.getName();
                    if (fileName.startsWith("record_")) {
                        long timestamp = Long.parseLong(fileName.substring(7));
                        Object obj;

                        obj = new JSONParser().parse(new FileReader(file.getAbsolutePath()));

                        JSONObject jo = (JSONObject) obj;
                        hm.put(timestamp, jo);
                    }
                }
            }
            List<Map.Entry<Long, JSONObject>> entries = new ArrayList<Map.Entry<Long, JSONObject>>(hm.entrySet());
            Collections.sort(entries, new Comparator<Map.Entry<Long, JSONObject>>() {
                public int compare(Map.Entry<Long, JSONObject> a, Map.Entry<Long, JSONObject> b){
                    return a.getKey().compareTo(b.getKey());
                }
            });
            sortedMap = new LinkedHashMap<>();
            for (Map.Entry<Long, JSONObject> entry : entries) {
                sortedMap.put(entry.getKey(), entry.getValue());
            }

//            sortedMap.forEach((timestamp, jsonData) -> {
//                System.out.println(timestamp + " : " + jsonData.toJSONString());
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sortedMap;
    }

    public void generate(String root) {
        String dataPath = root + "/history";
        String targetPath = root + "/graph";
        DefaultCategoryDataset dataset = createDataset(dataPath);
        // System.out.println("sdfsdfsdfsdfsd" +dataset.getRowCount());
        try {
            JFreeChart chart = ChartFactory.createLineChart(
                "Scanresult", // Chart title
                "Date", // X-Axis Label
                "Number of Alerts", // Y-Axis Label
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
            );
            ChartUtils.saveChartAsPNG(new File(targetPath + "/histogram.png"), chart, 1600, 800);
        } catch (Exception e) {
            System.out.println(e); 
        }
    }

    // public static void main(String[] args) throws IOException {
    //     generateGraph gg = new generateGraph();

    //     JFreeChart chart = ChartFactory.createLineChart(
    //             "Scanresult", // Chart title
    //             "Date", // X-Axis Label
    //             "Number of Alerts", // Y-Axis Label
    //             gg.createDataset()
    //     );
    //     ChartUtils.saveChartAsPNG(new File("./data/graph/histogram.png"), chart, 1600, 800);


    // }
}
