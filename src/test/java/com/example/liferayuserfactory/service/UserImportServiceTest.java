package com.example.liferayuserfactory.service;

import com.example.liferayuserfactory.config.LiferayProperties;
import com.example.liferayuserfactory.model.ImportResult;
import com.example.liferayuserfactory.model.UserRecord;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserImportServiceTest {

    @Test
    void marksInvalidEmailsAsFailures() throws IOException, LiferayException {
        ExcelUserParser parser = mock(ExcelUserParser.class);
        LiferayClient client = mock(LiferayClient.class);
        LiferayProperties properties = new LiferayProperties();
        List<UserRecord> parsedUsers = List.of(
                new UserRecord("valid@example.com", "First", "User"),
                new UserRecord("invalid-email", "Bad", "Address")
        );

        when(parser.parse(any())).thenReturn(parsedUsers);
        when(client.findMissingRoles(properties.getDefaultRoleIds())).thenReturn(List.of());
        when(client.userExists("valid@example.com")).thenReturn(false);
        doNothing().when(client).createUser(parsedUsers.get(0), 1L);

        UserImportService service = new UserImportService(parser, client, properties);
        MockMultipartFile file = new MockMultipartFile("file", new byte[0]);

        ImportResult result = service.importUsers(file, 1L);

        assertEquals(2, result.getTotalRows());
        assertEquals(1, result.getCreated());
        assertEquals(1, result.getFailures().size());
        assertTrue(result.getFailures().get(0).getReason().contains("Invalid email"));
    }
}
