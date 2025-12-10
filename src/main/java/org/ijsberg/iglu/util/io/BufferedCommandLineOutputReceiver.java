package org.ijsberg.iglu.util.io;

public class BufferedCommandLineOutputReceiver implements Receiver {

    private StringBuilder bufferedOutput = new StringBuilder();

    @Override
    public void onReceive(Object message) {
        bufferedOutput.append(getStringFromCommandLineInput(message) + "\n");
    }

    @Override
    public void onTransmissionClose() {

    }

    public String getOutput() {
        return bufferedOutput.toString();
    }

    public static String getStringFromCommandLineInput(Object message) {
        String output = "";
        if(message == null) {
            return "";
        } else if (message instanceof byte[]) {
            output = new String((byte[]) message);
        } else {
            output = "" + message;
        }
        return output;
    }

}
