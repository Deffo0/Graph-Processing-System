// package org.example.log;
// import java.util.logging.Formatter;
// import java.util.logging.LogRecord;
// import org.json.JSONObject;
// import java.text.SimpleDateFormat;
// import java.util.Date;

// public class JsonFormatter extends Formatter {

//     private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

//     @Override
//     public String format(LogRecord record) {
//         JSONObject json = new JSONObject();
//         try {
//             json.put("level", record.getLevel().getName());
//             json.put("message", record.getMessage());
//             json.put("loggerName", record.getLoggerName());
//             json.put("sourceClassName", record.getSourceClassName());
//             json.put("sourceMethodName", record.getSourceMethodName());
//             json.put("sequenceNumber", record.getSequenceNumber());
//             json.put("threadID", record.getThreadID());
//             json.put("longThreadID", record.getLongThreadID());
//             json.put("thrown", record.getThrown() == null ? null : record.getThrown().toString());
//             json.put("resourceBundleName", record.getResourceBundleName());
//             json.put("timestamp", dateFormat.format(Date.from(record.getInstant())));
//         } catch (org.json.JSONException e) {
//             System.err.println("JSONException: " + e.getMessage());
//             e.printStackTrace();
//         }
//         return json.toString() + "\n";
//     }
// }
