import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {
        int port = 9090;

        try {
            // 서버 소켓을 생성하고 포트 9090에서 대기
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("서버가 시작되었습니다. 클라이언트 기다리는중");

            // 클라이언트 연결 대기
            Socket clientSocket = serverSocket.accept();
            System.out.println("클라이언트 연결: " + clientSocket.getInetAddress()); // 소켓주소

            // 클라이언트와의 입출력 스트림 생성
            // 클라이언트 소켓에서 받아온 데이터를 스트림으로 꺼내서 BufferedReader로 읽어오겠다
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            // 클라이언트로 출력스트림 생성
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String message;
            // 버퍼에서 읽어올 줄이 없어질때까지 반복문 실행
            while ((message = in.readLine()) != null) {
                System.out.println("클라이언트 메시지: " + message);
                out.println("서버 응답 : " + message); // 클라이언트에 메세지 응답
            }

            // 자원반납
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
