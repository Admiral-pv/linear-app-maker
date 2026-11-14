package com.linearappmaker.projects;

import com.linearappmaker.users.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {
    @Test void updateChangesEditableFieldsButRetainsOwner() {
        var owner = new User("person@example.com", "encoded", "Person");
        var project = new Project(owner, "Old", "description", Project.Type.WEBSITE);
        project.update("New", null, Project.Type.APPLICATION);
        assertEquals("New", project.getName());
        assertNull(project.getDescription());
        assertEquals(Project.Type.APPLICATION, project.getType());
        assertSame(owner, project.getOwner());
    }
}
