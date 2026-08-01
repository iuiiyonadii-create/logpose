package com.thamis.lab.orchestrator

import com.thamis.lab.intelligence.architecture.ArchitectureValidatorEngine
import com.thamis.lab.intelligence.backlog.BacklogItem
import com.thamis.lab.intelligence.backlog.EngineeringBacklogStore
import com.thamis.lab.orchestrator.docs.DocumentationEngine
import com.thamis.lab.orchestrator.workflow.EngineeringWorkflowEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class Part4AutonomousModeTest {

    @Test
    fun testEngineeringWorkflowAndBacklogStore() {
        val workflowEngine = EngineeringWorkflowEngine()
        val backlogStore = EngineeringBacklogStore()

        val steps = workflowEngine.executeFullWorkflowCycle("task-101")
        assertEquals(15, steps.size)
        assertTrue(steps.all { it.isSuccess })

        backlogStore.addTask(BacklogItem("task-101", "Enhance ADB Stream", "Description", "CRITICAL", "LOW", "MEDIUM", 2.0, listOf(":lab:performance-farm"), "PENDING"))
        val prioritized = backlogStore.getPrioritizedTasks()
        assertEquals(1, prioritized.size)
        assertEquals("task-101", prioritized.first().taskId)
    }

    @Test
    fun testArchitectureValidatorAndDocsEngine() {
        val archValidator = ArchitectureValidatorEngine()
        val docEngine = DocumentationEngine()

        val archReport = archValidator.validateRepositoryArchitecture()
        assertTrue(archReport.isCleanArchitectureCompliant)
        assertEquals(0, archReport.circularDependenciesCount)

        val readme = docEngine.generateModuleReadme(":lab:orchestrator", "Orchestrates pipelines")
        assertTrue(readme.contains("# Module :lab:orchestrator"))
    }
}
