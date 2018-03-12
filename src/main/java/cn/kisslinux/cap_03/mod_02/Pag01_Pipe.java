package cn.kisslinux.cap_03.mod_02;

import org.junit.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * @author 庄壮壮 Administrator
 * @since 2018-03-12 19:00
 */
public class Pag01_Pipe {

    @Test
    public void testClient() throws IOException, InterruptedException {
        PipedInputStream inputStream = new PipedInputStream();
        PipedOutputStream outputStream = new PipedOutputStream();

        outputStream.connect(inputStream);

        Thread readThread = new Thread(new ReadData(inputStream));
        Thread writeThread = new Thread(new WriteData(outputStream));

        readThread.setName("ReadThread");
        writeThread.setName("WriteThread");

        readThread.start();
        Thread.sleep(2000);
        writeThread.start();

        Thread.sleep(500000);

    }

    class WriteData implements Runnable {

        private PipedOutputStream outputStream;

        public WriteData(PipedOutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public void run() {

            System.out.println("Write: ");
            try {
                for (int i=0; i<300; i++) {
                    String outData = "" + i;
                    outputStream.write(outData.getBytes());
                    Thread.sleep(500);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class ReadData implements Runnable{
        private PipedInputStream inputStream;

        public ReadData(PipedInputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            System.out.println("Read: ");
            try {
                byte[] bytes = new byte[20];
                int length = inputStream.read(bytes);
                while (length != -1) {
                    String newData = new String(bytes, 0, length);
                    System.out.println(newData);
                    length = inputStream.read(bytes);
                }
                System.out.println();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}