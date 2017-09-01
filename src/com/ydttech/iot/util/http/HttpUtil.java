package com.ydttech.iot.util.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;

public class HttpUtil {


    private String url;

    private Thread selfThread;
    private byte[] rspData;
    private boolean available = false;
    private ByteArrayOutputStream baos;

    public HttpUtil(String url) {
        this.url = url;
    }

    public void postStringData(final String reqData) {

        available = false;
        selfThread = new Thread(new Runnable() {

            CloseableHttpClient httpClient;
            HttpPost postRequest;

            @Override
            public void run() {

                if (!url.isEmpty()) {
                    httpClient = HttpClientBuilder.create().build();
                    postRequest = new HttpPost(url);

                    postRequest.setHeader("Content-Type", "application/json");
                    StringEntity parms = new StringEntity(reqData, "UTF-8");

                    postRequest.setEntity(parms);

                    HttpResponse response = null;
                    try {
                        response = httpClient.execute(postRequest);

                        if (response.getStatusLine().getStatusCode() != 200) {
                            throw new RuntimeException("Failed : HTTP error code : "
                                    + response.getStatusLine().getStatusCode());
                        }

                        baos = new ByteArrayOutputStream();

                        InputStream is = response.getEntity().getContent();
                        rspData = new byte[1024];

                        int numRead = 0;
                        while ((numRead = is.read(rspData)) > -1) {
                            baos.write(rspData, 0, numRead);
                        }
                        baos.write(reqData.getBytes(), 0, 3);
                        baos.flush();
                        available = true;
                        httpClient.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
            }
        });

        selfThread.start();
    }

    public boolean isAvailable() {
        return available;
    }

    public byte[] getPostStringData() {
        available = false;
        return baos.toByteArray();
    }

}
