import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Client {

    private static final int nThreads = 1;

    private static final String serverUri = "tcp://localhost:1883";
    private static final String topic = "ifood/restaurant/connection";
    private static final String schedule = "ifood/restaurant/schedule";

    private static boolean sendSchedule = false;

    private static MqttMessage createConnectionMessage(long restaurantId) {

        byte[] buffer = new byte[8];
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        byteBuffer.putLong(restaurantId);

        MqttMessage message = new MqttMessage(buffer);
        message.setQos(2);

        return message;
    }

    private static MqttMessage createScheduleMessage(long restaurantId) {

        byte[] buffer = new byte[24];
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        byteBuffer.putLong(restaurantId);
        byteBuffer.putLong(Instant.now().toEpochMilli());
        byteBuffer.putLong(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli());

        MqttMessage message = new MqttMessage(buffer);
        message.setQos(2);

        return message;
    }

    static class Publisher extends Thread {

        long id;

        public Publisher(long id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                MqttClient client = connect();

                while (!isInterrupted()) {

                    System.out.println("connected");

                    System.out.println("sending keep-alive");
                    client.publish(topic, createConnectionMessage(id));

                    if (sendSchedule) {
                        System.out.println("sending schedule");
                        client.publish(schedule, createScheduleMessage(id));
                    }

                    // Send one message at every minute to guarantee
                    // the 2 minutes limit
                    Thread.sleep(60 * 1000);


                }
            } catch (MqttException | InterruptedException e) {

                e.printStackTrace();
            }
        }

        private MqttClient connect() throws MqttException {

            System.out.println(id + " connecting");

            MqttConnectOptions connOptions = new MqttConnectOptions();
            connOptions.setCleanSession(false);
            connOptions.setKeepAliveInterval(10);

            MqttClient client = new MqttClient(serverUri, MqttClient.generateClientId());
            client.connect(connOptions);

            return client;
        }
    }

    public static void main(String[] args) {
        new Publisher(1).start();

        if (args.length > 0) {
            if ("schedule".equals(args[0])) {
                sendSchedule = true;
            }
        }
    }
}
