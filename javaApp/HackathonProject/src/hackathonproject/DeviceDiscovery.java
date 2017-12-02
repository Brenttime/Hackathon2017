//package hackathonproject;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.Vector;
// 
//import javax.bluetooth.BluetoothStateException;
//import javax.bluetooth.DeviceClass;
//import javax.bluetooth.DiscoveryAgent;
//import javax.bluetooth.DiscoveryListener;
//import javax.bluetooth.LocalDevice;
//import javax.bluetooth.RemoteDevice;
//import javax.bluetooth.ServiceRecord;
//import javax.bluetooth.UUID;
//import javax.microedition.io.Connector;
//import javax.microedition.io.StreamConnection;
//import javax.microedition.lcdui.Command;
//import javax.microedition.lcdui.CommandListener;
//import javax.microedition.lcdui.Display;
//import javax.microedition.lcdui.Displayable;
//import javax.microedition.lcdui.Form;
//import javax.microedition.lcdui.List;
//import javax.microedition.midlet.MIDlet;
//import javax.microedition.midlet.MIDletStateChangeException;
// 
///**
// * A class that demonstrates Bluetooth communication between client mode device
// * and server mode PC through serial port profile.
// */
//public class DeviceDiscovery implements DiscoveryListener, CommandListener {
// 
//    /*-
//     * ================
//     * Bluetooth Client
//     * ================
//     * 
//     * This example application is a straightforward implementation of 
//     * making a connection throught bluetooth link and using it. 
//     * 
//     * 
//     * Usage
//     * =====
//     * 
//     * Once the system is started the information area is filled with logging
//     * information. Wait until combo box is enabled (if there's any bluetooth 
//     * device around) and select a device from the combo box. A connection 
//     * is constructed to selected device after selection. 
//     * 
//     * After connection is constructed succesfully the application 
//     * operates as an echo client, i.e., everything that is read is written 
//     * back to the server. All information is also written into the information 
//     * area.
//     * 
//     * 
//     * How it Works
//     * ============
//     * 
//     * The example consist of three different operations. Operations need to 
//     * be executed in the dependency order. 
//     * 
//     * 1) *Inquiry method* is used to search all available devices. The combo 
//     *    box's data model is filled with found bluetooth devices.
//     *     
//     * 2) *Service search* is used to search serial port profile service from the
//     *    selected device. The search is started after the user selects the
//     *    device from the combo box.
//     *    
//     * 3) *Stream handling* communicates with the server through the bluetooth 
//     *    link. This example operates in echo loop until stop token is found.
//     * 
//     * 
//     * Special Debug Mode
//     * ==================
//     * 
//     * There's a special debug mode which speeds up development by skipping the
//     * inquiry method to resolve the remote device. In the debug mode the device's 
//     * bluetooth address is provided by the developer.
//     */
// 
//    /*-
//     * 
//     *  ---- Debug attributes ----
//     */
//    static final boolean DEBUG = false;
//    static final String DEBUG_address = "0013FDC157C8"; // N6630
// 
//    /*-
//     * 
//     *  ---- Bluetooth attributes ----
//     */
//    protected UUID uuid = new UUID(0x1101); // serial port profile
// 
//    protected int inquiryMode = DiscoveryAgent.GIAC; // no pairing is needed
// 
//    protected int connectionOptions = ServiceRecord.NOAUTHENTICATE_NOENCRYPT;
// 
//    /*-
//     * 
//     *  ---- Echo loop attributes ----
//     */
//    protected int stopToken = 255;
// 
//    /*-
//     * 
//     *  ---- GUI attributes ----
//     */
//    private Command backCommand = new Command("Back", Command.BACK, 1);
//    protected Form infoArea = new Form("Bluetooth Client");
//    protected Vector deviceList = new Vector();
//    private CameraMIDlet mymidlet;
//    private byte[] imag;
// 
//    public DeviceClientCOMM(CameraMIDlet m, byte[] imag) {
//        mymidlet = m;
//        this.imag = imag;
//        infoArea.setCommandListener(this);
//        infoArea.addCommand(backCommand);
//        try {
//            startApp();
//        } catch (MIDletStateChangeException ex) {
//            ex.printStackTrace();
//        }
//    }
// 
//    protected void startApp() throws MIDletStateChangeException {
// 
//        makeInformationAreaGUI();
// 
//        if (DEBUG) // skip inquiry in debug mode
//        {
//            startServiceSearch(new RemoteDevice(DEBUG_address) {
//            });
//        } else {
//            try {
//                startDeviceInquiry();
//            } catch (Throwable t) {
//                log(t);
//            }
//        }
//    }
// 
//    /*-
//     *   -------  Device inquiry section -------
//     */
//    private void startDeviceInquiry() {
//        try {
//            log("Start inquiry method - this will take few seconds...");
//            DiscoveryAgent agent = getAgent();
//            agent.startInquiry(inquiryMode, this);
//             
//        } catch (Exception e) {
//            log(e);
//        }
//    }
// 
//    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
//        log("A device discovered (" + getDeviceStr(btDevice) + ")");
//        deviceList.addElement(btDevice);
//    }
// 
//    public void inquiryCompleted(int discType) {
//        log("Inquiry compeleted. Please select device from combo box.");
//        makeDeviceSelectionGUI();
//    }
// 
//    /*-
//     *   -------  Service search section -------
//     */
//    private void startServiceSearch(RemoteDevice device) {
//        try {
//            log("Start search for Serial Port Profile service from " + getDeviceStr(device));
//            UUID uuids[] = new UUID[]{uuid};
//            getAgent().searchServices(null, uuids, device, this);
//        } catch (Exception e) {
//            log(e);
//        }
//    }
// 
//    /**
//     * This method is called when a service(s) are discovered.This method starts
//     * a thread that handles the data exchange with the server.
//     */
//    public void servicesDiscovered(int transId, ServiceRecord[] records) {
//        log("Service discovered.");
//        for (int i = 0; i < records.length; i++) {
//            ServiceRecord rec = records[i];
//            String url = rec.getConnectionURL(connectionOptions, false);
//            handleConnection(url);
//        }
//    }
// 
//    public void serviceSearchCompleted(int transID, int respCode) {
//        String msg = null;
//        switch (respCode) {
//            case SERVICE_SEARCH_COMPLETED:
//                msg = "the service search completed normally";
//                break;
//            case SERVICE_SEARCH_TERMINATED:
//                msg = "the service search request was cancelled by a call to DiscoveryAgent.cancelServiceSearch()";
//                break;
//            case SERVICE_SEARCH_ERROR:
//                msg = "an error occurred while processing the request";
//                break;
//            case SERVICE_SEARCH_NO_RECORDS:
//                msg = "no records were found during the service search";
//                break;
//            case SERVICE_SEARCH_DEVICE_NOT_REACHABLE:
//                msg = "the device specified in the search request could not be reached or the local device could not establish a connection to the remote device";
//                break;
//        }
//        log("Service search completed - " + msg);
//    }
// 
//    /*-
//     *   -------  The actual connection handling. -------
//     */
//    private void handleConnection(final String url) {
//        Thread echo = new Thread() {
// 
//            public void run() {
//                StreamConnection stream = null;
//                InputStream in = null;
//                OutputStream out = null;
// 
//                try {
//                    log("Connecting to server by url: " + url);
//                    stream = (StreamConnection) Connector.open(url);
// 
//                    log("Bluetooth stream open.");
//                    //   InputStream in = stream.openInputStream();
//                    out = stream.openOutputStream();
//                    in = stream.openInputStream();
//                    startReadThread(in);
//                    // String stringImage = Base64.encode(imag);
//                    log("Start echo loop.");
//                    out.write(imag);
//                    out.flush();
//                //       out.flush();
// 
//                //   stream.close();
// 
//                } catch (IOException e) {
//                    log(e);
//                } finally {
//                    log("Bluetooth stream closed.");
//                    if (out != null) {
//                        try {
// 
//                            out.close();
//                            stream.close();
// 
//                            logSet("Image Transfer done\n----------------\n\nWaiting for results...");
//                        } catch (IOException e) {
//                            log(e);
//                        }
//                    }
//                }
//            }
//        };
//        echo.start();
//    }
// 
//    private void startReadThread(final InputStream in) {
// 
//        Thread reader = new Thread() {
// 
//            public void run() {
// 
//                byte[] s = new byte[512];
//                //boolean flag = true;
//                try {
//                    while (true) {
//                        int r = in.read(s);
// 
//                        if (r != -1) {
//                            
//                            logSet(new String(s, 0, r));
//                        }  else {
//                            break;
//                        }
// 
// 
//                        Thread.sleep(200);
//                    }
//                } catch (Throwable e) {
//                    log(e);
// 
//                } finally {
//                    if (in != null) {
//                        try {
//                            in.close();
//                        } catch (IOException e) {
//                        }
//                    }
//                }
//            }
//        };
//        reader.start();
//    }
// 
//    /*-
//     *   -------  Graphic User Interface section -------
//     */
//    private void makeInformationAreaGUI() {
//        infoArea.deleteAll();
//        Display.getDisplay(mymidlet).setCurrent(infoArea);
//    }
// 
//    private void makeDeviceSelectionGUI() {
//        final List devices = new List("Select a device", List.IMPLICIT);
//        for (int i = 0; i < deviceList.size(); i++) {
//            devices.append(
//                    getDeviceStr((RemoteDevice) deviceList.elementAt(i)), null);
//        }
//        devices.setCommandListener(new 
// 
//              CommandListener(   ) {
// 
//                 
//                  
//             
//         
//         
//     
// 
//    public  void commandAction(Command arg0, 
//        Displayable arg1) 
//        {
//                makeInformationAreaGUI();
//                startServiceSearch((RemoteDevice) deviceList.elementAt(devices.getSelectedIndex()));
//            }
//        });
//        Display.getDisplay(mymidlet).setCurrent(devices);
//    }
// 
//    synchronized private void log(String msg) {
//        infoArea.append(msg);
//        infoArea.append("\n\n");
//    }
// 
//    synchronized private void logSet(String msg) {
//        infoArea.deleteAll();
//        infoArea.append(msg);
//        infoArea.append("\n\n");
// 
//    }
// 
//    private void log(Throwable e) {
//        log(e.getMessage());
//    }
// 
//    /*-
//     *   -------  Utils section - contains utility functions -------
//     */
//    private DiscoveryAgent getAgent() {
//        try {
//            return LocalDevice.getLocalDevice().getDiscoveryAgent();
//        } catch (BluetoothStateException e) {
//            throw new Error(e.getMessage());
//        }
//    }
// 
//    private String getDeviceStr(RemoteDevice btDevice) {
//        return getFriendlyName(btDevice) + " - 0x" + btDevice.getBluetoothAddress();
//    }
// 
//    private String getFriendlyName(RemoteDevice btDevice) {
//        try {
//            return btDevice.getFriendlyName(false);
//        } catch (IOException e) {
//            return "no name available";
//        }
//    }
// 
//    public void commandAction(Command arg0, Displayable arg1) {
//        mymidlet.DisplayMainList();
//    }
//}
