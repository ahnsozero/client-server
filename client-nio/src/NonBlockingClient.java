import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NonBlockingClient {

    public static void main(String[] args) {
        try(
            Socket socket = new Socket("localhost", 9090);
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ) {

            System.out.println("서버에 연결됨. 메시지를 입력하세요:");

            String userInput;
            while ((userInput = input.readLine()) != null) {
               out.println(userInput);
               String response = in.readLine();
               System.out.println("서버 응답: " + response);

               if(userInput.equalsIgnoreCase("exit")) break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
