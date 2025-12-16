package com.example.liferayuserfactory.service;

import com.example.liferayuserfactory.config.LiferayProperties;
import com.example.liferayuserfactory.model.ImportResult;
import com.example.liferayuserfactory.model.LiferayUser;
import com.example.liferayuserfactory.model.UserRecord;
import com.example.liferayuserfactory.repository.LiferayUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserImportServiceTest {

    @Test
    void marksInvalidEmailsAsFailures() throws IOException, LiferayException {
        ExcelUserParser parser = mock(ExcelUserParser.class);
        LiferayClient client = mock(LiferayClient.class);
        LiferayUserRepository repository = mock(LiferayUserRepository.class);
        LiferayProperties properties = new LiferayProperties();
        List<UserRecord> parsedUsers = List.of(
                new UserRecord("valid@example.com", "First", "User"),
                new UserRecord("invalid-email", "Bad", "Address")
        );

        when(parser.parse(any())).thenReturn(parsedUsers);
        when(client.findMissingRoles(properties.getDefaultRoleIds())).thenReturn(List.of());
        when(repository.findByEmailAddressIgnoreCase("valid@example.com")).thenReturn(Optional.empty());
        when(client.userExists("valid@example.com")).thenReturn(false);
        doNothing().when(client).createUser(parsedUsers.get(0), 1L);

        UserImportService service = new UserImportService(parser, client, properties, repository);
        MockMultipartFile file = new MockMultipartFile("file", new byte[0]);

        ImportResult result = service.importUsers(file, 1L);

        assertEquals(2, result.getTotalRows());
        assertEquals(1, result.getCreated());
        assertEquals(1, result.getFailures().size());
        assertTrue(result.getFailures().get(0).getReason().contains("Invalid email"));
    }

    @Test
    void listsExistingUsersInsteadOfCreatingDuplicates() throws IOException, LiferayException {
        ExcelUserParser parser = mock(ExcelUserParser.class);
        LiferayClient client = mock(LiferayClient.class);
        LiferayUserRepository repository = mock(LiferayUserRepository.class);
        LiferayProperties properties = new LiferayProperties();
        UserRecord existingRecord = new UserRecord("duplicate@example.com", "Existing", "User");

        LiferayUser existingUser = mock(LiferayUser.class);
        when(existingUser.getId()).thenReturn(123L);
        when(existingUser.getEmailAddress()).thenReturn(existingRecord.getEmail());
        when(repository.findByEmailAddressIgnoreCase(existingRecord.getEmail()))
                .thenReturn(Optional.of(existingUser));
        when(parser.parse(any())).thenReturn(List.of(existingRecord));
        when(client.findMissingRoles(properties.getDefaultRoleIds())).thenReturn(List.of());

        UserImportService service = new UserImportService(parser, client, properties, repository);
        MockMultipartFile file = new MockMultipartFile("file", new byte[0]);

        ImportResult result = service.importUsers(file, 1L);

        assertEquals(0, result.getCreated());
        assertEquals(1, result.getExistingUsers().size());
        assertEquals(existingRecord.getEmail(), result.getExistingUsers().get(0).getEmailAddress());
        verify(client, never()).createUser(existingRecord, 1L);
        verify(client, never()).userExists(existingRecord.getEmail());
    }
}
