package com.example.devolucionmaterial.utils.progress;

public interface ProgressListener {

    void progress(long bytesRead, long contentLength, boolean done);

}
