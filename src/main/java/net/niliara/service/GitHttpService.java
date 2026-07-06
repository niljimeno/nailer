package net.niliara.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.niliara.dto.Credentials;
import net.niliara.dto.DataDirectory;
import net.niliara.dto.RepositoryId;

@Service
public class GitHttpService {
    private final DataDirectory dataDirectory;

    public GitHttpService(DataDirectory dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public void pipe(
            RepositoryId identifier,
            String path,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        ProcessBuilder builder = new ProcessBuilder("git", "http-backend");
        Map<String, String> environment = builder.environment();

        environment.put("GIT_PROJECT_ROOT", dataDirectory.dataDirectory().toString());
        environment.put("GIT_HTTP_EXPORT_ALL", "");
        environment.put("REQUEST_METHOD", request.getMethod());
        environment.put("PATH_INFO", "/" + identifier.username() + "/" + identifier.repositoryName() + path);
        environment.put("QUERY_STRING", request.getQueryString() == null ? "" : request.getQueryString());
        environment.put("REMOTE_USER", identifier.username());

        if (request.getContentType() != null) {
            environment.put("CONTENT_TYPE", request.getContentType());
        }

        if (request.getContentLengthLong() >= 0) {
            environment.put("CONTENT_LENGTH", Long.toString(request.getContentLengthLong()));
        }

        Process process = builder.start();

        try (var output = process.getOutputStream()) {
            request.getInputStream().transferTo(output);
        }

        writeGitHttpResponse(process.getInputStream(), response);
    }

    private void writeGitHttpResponse(InputStream input, HttpServletResponse response) throws IOException {
        String headers = new String(readGitHttpHeaders(input), StandardCharsets.ISO_8859_1);

        for (String header : headers.split("\\r?\\n")) {
            int separator = header.indexOf(":");
            if (separator < 0) {
                continue;
            }

            String name = header.substring(0, separator);
            String value = header.substring(separator + 1).trim();

            if (name.equalsIgnoreCase("Status")) {
                response.setStatus(Integer.parseInt(value.substring(0, 3)));
            } else {
                response.setHeader(name, value);
            }
        }

        input.transferTo(response.getOutputStream());
    }

    private byte[] readGitHttpHeaders(InputStream input) throws IOException {
        ByteArrayOutputStream headers = new ByteArrayOutputStream();
        int value;

        while ((value = input.read()) != -1) {
            headers.write(value);
            String text = headers.toString(StandardCharsets.ISO_8859_1);

            if (text.endsWith("\r\n\r\n") || text.endsWith("\n\n")) {
                return headers.toByteArray();
            }
        }

        return headers.toByteArray();
    }

    public Optional<Credentials> getUserCredentials(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        String credentials = "";
        if (authorization != null && authorization.startsWith("Basic ")) {
            try {
                credentials = new String(Base64.getDecoder().decode(authorization.substring(6)),
                        StandardCharsets.UTF_8);
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }

        int separator = credentials.indexOf(":");
        if (separator < 0) {
            return Optional.empty();
        }

        return Optional.of(new Credentials(
                credentials.substring(0, separator),
                credentials.substring(separator + 1)));
    }

    public void Unauthorize(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader("WWW-Authenticate", "Basic realm=\"nailer\"");
    }
}
