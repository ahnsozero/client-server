import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiThreadsServer {

    public static void main(String[] args) {
        int port = 9090;

        // try ( ... ) { ... } 구문은 Java의 try-with-resources 문법
        // 자동으로 자원을 닫아줌
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // 서버 소켓을 생성하고 포트 9090에서 대기
            System.out.println("서버가 시작되었습니다. 클라이언트 기다리는중");

            while (true) {
                // 클라이언트 연결 대기
                Socket clientSocket = serverSocket.accept();
                System.out.println("클라이언트 연결: " + clientSocket.getInetAddress());

                // 클라이언트별 쓰래드 생성하고 실행
                // start()를 호출해야 내부적으로 run() 메서드가 실행
                // run() 메서드를 직접 실행하면 새로운 스레드가 만들어지지않고 병렬 처리(멀티스레딩)가 일어나지 않음
                new ClientHandler(clientSocket).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// 하나의 클라이언트가 접속할시, 해당 클라이언트를 담당할 새로운 쓰래드 생성
class ClientHandler extends Thread { // 쓰레드 상속
    private Socket socket;

    // 생성자
    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    // 쓰레드 클래스 오버라이딩
    public void run() {
        try (
            // 클라이언트와의 입출력 스트림 생성
            // 클라이언트 소켓에서 받아온 데이터를 스트림으로 꺼내서 BufferedReader로 읽어오겠다
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
            // 클라이언트로 출력스트림 생성
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {
            String message;
            // 버퍼에서 읽어올 줄이 없어질때까지 반복문 실행
            while ((message = in.readLine()) != null) {
                System.out.println("클라이언트 메세지: " + message);
                out.println("서버 응답: " + message); // 클라이언트에 메세지 응답
            }
            socket.close(); // 소켓 반납
            System.out.println("클라이언트 연결 끊김");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}