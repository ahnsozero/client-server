import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MultiThreadsClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 9090;

        // try ( ... ) { ... } 구문은 Java의 try-with-resources 문법
        // 자동으로 자원을 닫아줌
        try (
            // 클라이언트 소켓 포트 9090에 연결
            Socket socket = new Socket(host, port); // 소켓 생성
            // 서버와의 입출력 스트림 생성
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            // 사용자로부터 메시지 입력
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        ) {
            String message;
            while (true) {
                System.out.println("클라이언트 메시지: ");
                message = userInput.readLine();;
                // exit라고 사용자가 입력했을시 break;
                // cf) equalsIgnoreCase 경우, 대소문자 구분X
                if (message.equalsIgnoreCase("exit")) {
                    break;
                }

                // 서버로 메시지 전송
                out.println(message);

                // 서버 응답 받기
                System.out.println("Server: " + in.readLine());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
