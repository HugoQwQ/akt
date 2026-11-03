package io.hugouwu.akt.render

import io.hugouwu.akt.assets.AssetStore
import io.hugouwu.akt.core.AssetKey
import io.hugouwu.akt.core.Color
import io.hugouwu.akt.core.Rect
import io.hugouwu.akt.core.TextAlign
import io.hugouwu.akt.core.Transform
import io.hugouwu.akt.core.Vec2
import io.hugouwu.akt.ui.ChoiceItem
import io.hugouwu.akt.ui.ImageItem
import io.hugouwu.akt.ui.PanelItem
import io.hugouwu.akt.ui.TextItem
import io.hugouwu.akt.ui.UiItem
import io.hugouwu.akt.ui.UiScene
import java.awt.image.BufferedImage
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import org.lwjgl.glfw.GLFW.GLFW_CLIENT_API
import org.lwjgl.glfw.GLFW.GLFW_KEY_1
import org.lwjgl.glfw.GLFW.GLFW_KEY_2
import org.lwjgl.glfw.GLFW.GLFW_KEY_3
import org.lwjgl.glfw.GLFW.GLFW_KEY_4
import org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER
import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE
import org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ENTER
import org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE
import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT
import org.lwjgl.glfw.GLFW.GLFW_NO_API
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RESIZABLE
import org.lwjgl.glfw.GLFW.GLFW_TRUE
import org.lwjgl.glfw.GLFW.GLFW_VISIBLE
import org.lwjgl.glfw.GLFW.glfwCreateWindow
import org.lwjgl.glfw.GLFW.glfwDefaultWindowHints
import org.lwjgl.glfw.GLFW.glfwDestroyWindow
import org.lwjgl.glfw.GLFW.glfwGetCursorPos
import org.lwjgl.glfw.GLFW.glfwGetFramebufferSize
import org.lwjgl.glfw.GLFW.glfwGetKey
import org.lwjgl.glfw.GLFW.glfwGetMouseButton
import org.lwjgl.glfw.GLFW.glfwGetTime
import org.lwjgl.glfw.GLFW.glfwGetWindowSize
import org.lwjgl.glfw.GLFW.glfwInit
import org.lwjgl.glfw.GLFW.glfwPollEvents
import org.lwjgl.glfw.GLFW.glfwShowWindow
import org.lwjgl.glfw.GLFW.glfwTerminate
import org.lwjgl.glfw.GLFW.glfwWindowHint
import org.lwjgl.glfw.GLFW.glfwWindowShouldClose
import org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface
import org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions
import org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported
import org.lwjgl.stb.STBImage.stbi_image_free
import org.lwjgl.stb.STBImage.stbi_load_from_memory
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.memAlloc
import org.lwjgl.system.MemoryUtil.memByteBuffer
import org.lwjgl.system.MemoryUtil.memFree
import org.lwjgl.system.MemoryUtil.memUTF8
import org.lwjgl.vulkan.KHRPortabilityEnumeration.VK_INSTANCE_CREATE_ENUMERATE_PORTABILITY_BIT_KHR
import org.lwjgl.vulkan.KHRPortabilityEnumeration.VK_KHR_PORTABILITY_ENUMERATION_EXTENSION_NAME
import org.lwjgl.vulkan.KHRPortabilitySubset.VK_KHR_PORTABILITY_SUBSET_EXTENSION_NAME
import org.lwjgl.vulkan.KHRSurface.VK_COLOR_SPACE_SRGB_NONLINEAR_KHR
import org.lwjgl.vulkan.KHRSurface.VK_COMPOSITE_ALPHA_INHERIT_BIT_KHR
import org.lwjgl.vulkan.KHRSurface.VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR
import org.lwjgl.vulkan.KHRSurface.VK_PRESENT_MODE_FIFO_KHR
import org.lwjgl.vulkan.KHRSurface.VK_PRESENT_MODE_MAILBOX_KHR
import org.lwjgl.vulkan.KHRSurface.VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR
import org.lwjgl.vulkan.KHRSurface.vkDestroySurfaceKHR
import org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceCapabilitiesKHR
import org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR
import org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfacePresentModesKHR
import org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR
import org.lwjgl.vulkan.KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR
import org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME
import org.lwjgl.vulkan.KHRSwapchain.VK_STRUCTURE_TYPE_PRESENT_INFO_KHR
import org.lwjgl.vulkan.KHRSwapchain.vkAcquireNextImageKHR
import org.lwjgl.vulkan.KHRSwapchain.vkCreateSwapchainKHR
import org.lwjgl.vulkan.KHRSwapchain.vkDestroySwapchainKHR
import org.lwjgl.vulkan.KHRSwapchain.vkGetSwapchainImagesKHR
import org.lwjgl.vulkan.KHRSwapchain.vkQueuePresentKHR
import org.lwjgl.vulkan.VK10.VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT
import org.lwjgl.vulkan.VK10.VK_ACCESS_SHADER_READ_BIT
import org.lwjgl.vulkan.VK10.VK_ACCESS_TRANSFER_READ_BIT
import org.lwjgl.vulkan.VK10.VK_ACCESS_TRANSFER_WRITE_BIT
import org.lwjgl.vulkan.VK10.VK_API_VERSION_1_0
import org.lwjgl.vulkan.VK10.VK_ATTACHMENT_LOAD_OP_CLEAR
import org.lwjgl.vulkan.VK10.VK_ATTACHMENT_LOAD_OP_DONT_CARE
import org.lwjgl.vulkan.VK10.VK_ATTACHMENT_STORE_OP_DONT_CARE
import org.lwjgl.vulkan.VK10.VK_ATTACHMENT_STORE_OP_STORE
import org.lwjgl.vulkan.VK10.VK_BLEND_FACTOR_ONE
import org.lwjgl.vulkan.VK10.VK_BLEND_FACTOR_ONE_MINUS_SRC_ALPHA
import org.lwjgl.vulkan.VK10.VK_BLEND_FACTOR_SRC_ALPHA
import org.lwjgl.vulkan.VK10.VK_BLEND_OP_ADD
import org.lwjgl.vulkan.VK10.VK_BORDER_COLOR_FLOAT_OPAQUE_BLACK
import org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_TRANSFER_DST_BIT
import org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_TRANSFER_SRC_BIT
import org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT
import org.lwjgl.vulkan.VK10.VK_COLOR_COMPONENT_A_BIT
import org.lwjgl.vulkan.VK10.VK_COLOR_COMPONENT_B_BIT
import org.lwjgl.vulkan.VK10.VK_COLOR_COMPONENT_G_BIT
import org.lwjgl.vulkan.VK10.VK_COLOR_COMPONENT_R_BIT
import org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_LEVEL_PRIMARY
import org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT
import org.lwjgl.vulkan.VK10.VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT
import org.lwjgl.vulkan.VK10.VK_COMPONENT_SWIZZLE_IDENTITY
import org.lwjgl.vulkan.VK10.VK_CULL_MODE_NONE
import org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER
import org.lwjgl.vulkan.VK10.VK_DYNAMIC_STATE_SCISSOR
import org.lwjgl.vulkan.VK10.VK_DYNAMIC_STATE_VIEWPORT
import org.lwjgl.vulkan.VK10.VK_FENCE_CREATE_SIGNALED_BIT
import org.lwjgl.vulkan.VK10.VK_FILTER_LINEAR
import org.lwjgl.vulkan.VK10.VK_FORMAT_B8G8R8A8_SRGB
import org.lwjgl.vulkan.VK10.VK_FORMAT_B8G8R8A8_UNORM
import org.lwjgl.vulkan.VK10.VK_FORMAT_R8G8B8A8_SRGB
import org.lwjgl.vulkan.VK10.VK_FORMAT_R8G8B8A8_UNORM
import org.lwjgl.vulkan.VK10.VK_FRONT_FACE_COUNTER_CLOCKWISE
import org.lwjgl.vulkan.VK10.VK_IMAGE_ASPECT_COLOR_BIT
import org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL
import org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL
import org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL
import org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_UNDEFINED
import org.lwjgl.vulkan.VK10.VK_IMAGE_TILING_OPTIMAL
import org.lwjgl.vulkan.VK10.VK_IMAGE_TYPE_2D
import org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_SAMPLED_BIT
import org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_TRANSFER_DST_BIT
import org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_TRANSFER_SRC_BIT
import org.lwjgl.vulkan.VK10.VK_IMAGE_VIEW_TYPE_2D
import org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT
import org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_HOST_COHERENT_BIT
import org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT
import org.lwjgl.vulkan.VK10.VK_NULL_HANDLE
import org.lwjgl.vulkan.VK10.VK_PIPELINE_BIND_POINT_GRAPHICS
import org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT
import org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT
import org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_TRANSFER_BIT
import org.lwjgl.vulkan.VK10.VK_POLYGON_MODE_FILL
import org.lwjgl.vulkan.VK10.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST
import org.lwjgl.vulkan.VK10.VK_QUEUE_FAMILY_IGNORED
import org.lwjgl.vulkan.VK10.VK_QUEUE_GRAPHICS_BIT
import org.lwjgl.vulkan.VK10.VK_SAMPLE_COUNT_1_BIT
import org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_FRAGMENT_BIT
import org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_VERTEX_BIT
import org.lwjgl.vulkan.VK10.VK_SHARING_MODE_EXCLUSIVE
import org.lwjgl.vulkan.VK10.VK_SUBPASS_EXTERNAL
import org.lwjgl.vulkan.VK10.VK_SUCCESS
import org.lwjgl.vulkan.VK10.VK_VERTEX_INPUT_RATE_VERTEX
import org.lwjgl.vulkan.VK10.vkAllocateCommandBuffers
import org.lwjgl.vulkan.VK10.vkAllocateDescriptorSets
import org.lwjgl.vulkan.VK10.vkAllocateMemory
import org.lwjgl.vulkan.VK10.vkBeginCommandBuffer
import org.lwjgl.vulkan.VK10.vkBindBufferMemory
import org.lwjgl.vulkan.VK10.vkBindImageMemory
import org.lwjgl.vulkan.VK10.vkCmdBeginRenderPass
import org.lwjgl.vulkan.VK10.vkCmdBindDescriptorSets
import org.lwjgl.vulkan.VK10.vkCmdBindPipeline
import org.lwjgl.vulkan.VK10.vkCmdBindVertexBuffers
import org.lwjgl.vulkan.VK10.vkCmdCopyBufferToImage
import org.lwjgl.vulkan.VK10.vkCmdCopyImageToBuffer
import org.lwjgl.vulkan.VK10.vkCmdDraw
import org.lwjgl.vulkan.VK10.vkCmdEndRenderPass
import org.lwjgl.vulkan.VK10.vkCmdPipelineBarrier
import org.lwjgl.vulkan.VK10.vkCmdPushConstants
import org.lwjgl.vulkan.VK10.vkCmdSetScissor
import org.lwjgl.vulkan.VK10.vkCmdSetViewport
import org.lwjgl.vulkan.VK10.vkCreateBuffer
import org.lwjgl.vulkan.VK10.vkCreateCommandPool
import org.lwjgl.vulkan.VK10.vkCreateDescriptorPool
import org.lwjgl.vulkan.VK10.vkCreateDescriptorSetLayout
import org.lwjgl.vulkan.VK10.vkCreateDevice
import org.lwjgl.vulkan.VK10.vkCreateFence
import org.lwjgl.vulkan.VK10.vkCreateFramebuffer
import org.lwjgl.vulkan.VK10.vkCreateGraphicsPipelines
import org.lwjgl.vulkan.VK10.vkCreateImage
import org.lwjgl.vulkan.VK10.vkCreateImageView
import org.lwjgl.vulkan.VK10.vkCreateInstance
import org.lwjgl.vulkan.VK10.vkCreatePipelineLayout
import org.lwjgl.vulkan.VK10.vkCreateRenderPass
import org.lwjgl.vulkan.VK10.vkCreateSampler
import org.lwjgl.vulkan.VK10.vkCreateSemaphore
import org.lwjgl.vulkan.VK10.vkCreateShaderModule
import org.lwjgl.vulkan.VK10.vkDestroyBuffer
import org.lwjgl.vulkan.VK10.vkDestroyCommandPool
import org.lwjgl.vulkan.VK10.vkDestroyDescriptorPool
import org.lwjgl.vulkan.VK10.vkDestroyDescriptorSetLayout
import org.lwjgl.vulkan.VK10.vkDestroyDevice
import org.lwjgl.vulkan.VK10.vkDestroyFence
import org.lwjgl.vulkan.VK10.vkDestroyFramebuffer
import org.lwjgl.vulkan.VK10.vkDestroyImage
import org.lwjgl.vulkan.VK10.vkDestroyImageView
import org.lwjgl.vulkan.VK10.vkDestroyInstance
import org.lwjgl.vulkan.VK10.vkDestroyPipeline
import org.lwjgl.vulkan.VK10.vkDestroyPipelineLayout
import org.lwjgl.vulkan.VK10.vkDestroyRenderPass
import org.lwjgl.vulkan.VK10.vkDestroySampler
import org.lwjgl.vulkan.VK10.vkDestroySemaphore
import org.lwjgl.vulkan.VK10.vkDestroyShaderModule
import org.lwjgl.vulkan.VK10.vkDeviceWaitIdle
import org.lwjgl.vulkan.VK10.vkEndCommandBuffer
import org.lwjgl.vulkan.VK10.vkEnumerateDeviceExtensionProperties
import org.lwjgl.vulkan.VK10.vkEnumerateInstanceExtensionProperties
import org.lwjgl.vulkan.VK10.vkEnumeratePhysicalDevices
import org.lwjgl.vulkan.VK10.vkFreeMemory
import org.lwjgl.vulkan.VK10.vkGetBufferMemoryRequirements
import org.lwjgl.vulkan.VK10.vkGetDeviceQueue
import org.lwjgl.vulkan.VK10.vkGetImageMemoryRequirements
import org.lwjgl.vulkan.VK10.vkGetPhysicalDeviceFeatures
import org.lwjgl.vulkan.VK10.vkGetPhysicalDeviceFormatProperties
import org.lwjgl.vulkan.VK10.vkGetPhysicalDeviceMemoryProperties
import org.lwjgl.vulkan.VK10.vkGetPhysicalDeviceProperties
import org.lwjgl.vulkan.VK10.vkGetPhysicalDeviceQueueFamilyProperties
import org.lwjgl.vulkan.VK10.vkMapMemory
import org.lwjgl.vulkan.VK10.vkQueueSubmit
import org.lwjgl.vulkan.VK10.vkQueueWaitIdle
import org.lwjgl.vulkan.VK10.vkResetCommandBuffer
import org.lwjgl.vulkan.VK10.vkResetFences
import org.lwjgl.vulkan.VK10.vkUnmapMemory
import org.lwjgl.vulkan.VK10.vkUpdateDescriptorSets
import org.lwjgl.vulkan.VK10.vkWaitForFences
import org.lwjgl.vulkan.VkApplicationInfo
import org.lwjgl.vulkan.VkAttachmentDescription
import org.lwjgl.vulkan.VkAttachmentReference
import org.lwjgl.vulkan.VkBufferCreateInfo
import org.lwjgl.vulkan.VkBufferImageCopy
import org.lwjgl.vulkan.VkClearValue
import org.lwjgl.vulkan.VkCommandBuffer
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo
import org.lwjgl.vulkan.VkCommandBufferBeginInfo
import org.lwjgl.vulkan.VkCommandPoolCreateInfo
import org.lwjgl.vulkan.VkDescriptorImageInfo
import org.lwjgl.vulkan.VkDescriptorPoolCreateInfo
import org.lwjgl.vulkan.VkDescriptorPoolSize
import org.lwjgl.vulkan.VkDescriptorSetAllocateInfo
import org.lwjgl.vulkan.VkDescriptorSetLayoutBinding
import org.lwjgl.vulkan.VkDescriptorSetLayoutCreateInfo
import org.lwjgl.vulkan.VkDevice
import org.lwjgl.vulkan.VkDeviceCreateInfo
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo
import org.lwjgl.vulkan.VkExtensionProperties
import org.lwjgl.vulkan.VkFenceCreateInfo
import org.lwjgl.vulkan.VkFormatProperties
import org.lwjgl.vulkan.VkFramebufferCreateInfo
import org.lwjgl.vulkan.VkGraphicsPipelineCreateInfo
import org.lwjgl.vulkan.VkImageCreateInfo
import org.lwjgl.vulkan.VkImageMemoryBarrier
import org.lwjgl.vulkan.VkImageViewCreateInfo
import org.lwjgl.vulkan.VkInstance
import org.lwjgl.vulkan.VkInstanceCreateInfo
import org.lwjgl.vulkan.VkMemoryAllocateInfo
import org.lwjgl.vulkan.VkMemoryRequirements
import org.lwjgl.vulkan.VkPhysicalDevice
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties
import org.lwjgl.vulkan.VkPhysicalDeviceProperties
import org.lwjgl.vulkan.VkPipelineColorBlendAttachmentState
import org.lwjgl.vulkan.VkPipelineColorBlendStateCreateInfo
import org.lwjgl.vulkan.VkPipelineDynamicStateCreateInfo
import org.lwjgl.vulkan.VkPipelineInputAssemblyStateCreateInfo
import org.lwjgl.vulkan.VkPipelineLayoutCreateInfo
import org.lwjgl.vulkan.VkPipelineMultisampleStateCreateInfo
import org.lwjgl.vulkan.VkPipelineRasterizationStateCreateInfo
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo
import org.lwjgl.vulkan.VkPipelineVertexInputStateCreateInfo
import org.lwjgl.vulkan.VkPipelineViewportStateCreateInfo
import org.lwjgl.vulkan.VkPresentInfoKHR
import org.lwjgl.vulkan.VkPushConstantRange
import org.lwjgl.vulkan.VkQueue
import org.lwjgl.vulkan.VkQueueFamilyProperties
import org.lwjgl.vulkan.VkRect2D
import org.lwjgl.vulkan.VkRenderPassBeginInfo
import org.lwjgl.vulkan.VkRenderPassCreateInfo
import org.lwjgl.vulkan.VkSamplerCreateInfo
import org.lwjgl.vulkan.VkSemaphoreCreateInfo
import org.lwjgl.vulkan.VkShaderModuleCreateInfo
import org.lwjgl.vulkan.VkSubmitInfo
import org.lwjgl.vulkan.VkSubpassDependency
import org.lwjgl.vulkan.VkSubpassDescription
import org.lwjgl.vulkan.VkSurfaceCapabilitiesKHR
import org.lwjgl.vulkan.VkSurfaceFormatKHR
import org.lwjgl.vulkan.VkSwapchainCreateInfoKHR
import org.lwjgl.vulkan.VkVertexInputAttributeDescription
import org.lwjgl.vulkan.VkVertexInputBindingDescription
import org.lwjgl.vulkan.VkViewport
import org.lwjgl.vulkan.VkWriteDescriptorSet

/**
 * default lwjgl vulkan desktop renderer.
 *
 * the renderer owns the glfw window, vulkan device, swapchain, graphics pipeline, textures, and
 * frame synchronization. use [RendererFactory] to inject another backend instead of constructing
 * this class directly.
 *
 * @param config window and presentation settings.
 * @param assets source used to resolve image items.
 * @throws IllegalStateException when glfw, vulkan, or the graphics device cannot be initialized.
 */
public class VulkanRenderer(
    private val config: WindowConfig = WindowConfig(),
    private val assets: AssetStore = AssetStore.empty,
) : RendererBackend {
    private data class QueueFamilies(
        val graphics: Int,
        val present: Int,
    )

    private data class SwapchainSupport(
        val minImageCount: Int,
        val maxImageCount: Int,
        val currentExtentWidth: Int,
        val currentExtentHeight: Int,
        val minExtentWidth: Int,
        val minExtentHeight: Int,
        val maxExtentWidth: Int,
        val maxExtentHeight: Int,
        val supportedUsageFlags: Int,
        val supportedCompositeAlpha: Int,
        val currentTransform: Int,
        val supportedTransforms: Int,
        val formats: List<SurfaceFormat>,
        val presentModes: List<Int>,
    )

    private data class SurfaceFormat(
        val format: Int,
        val colorSpace: Int,
    )

    private data class BufferAllocation(
        val buffer: Long,
        val memory: Long,
        val size: Long,
    )

    private data class TextureResource(
        val image: Long,
        val memory: Long,
        val view: Long,
        val descriptorSet: Long,
    )

    private data class DrawBatch(
        val texture: TextureResource,
        val vertices: MutableList<Float> = mutableListOf(),
    )

    private val instance: VkInstance
    private val surface: Long
    private val physicalDevice: VkPhysicalDevice
    private val device: VkDevice
    private val graphicsQueue: VkQueue
    private val presentQueue: VkQueue
    private val commandPool: Long
    private val commandBuffer: VkCommandBuffer
    private val imageAvailableSemaphore: Long
    private val inFlightFence: Long
    private val sampler: Long
    private val descriptorSetLayout: Long
    private val descriptorPool: Long
    private val pipelineLayout: Long
    private val fontAtlas: FontAtlas
    private val textures: MutableMap<AssetKey, TextureResource?> = hashMapOf()
    private val window: Long

    private var swapchain: Long = VK_NULL_HANDLE
    private var swapchainFormat: Int = VK_FORMAT_B8G8R8A8_SRGB
    private var swapchainExtentWidth: Int = 0
    private var swapchainExtentHeight: Int = 0
    private var swapchainImages: List<Long> = emptyList()
    private var swapchainImageViews: MutableList<Long> = mutableListOf()
    private var renderFinishedSemaphores: MutableList<Long> = mutableListOf()
    private var framebuffers: MutableList<Long> = mutableListOf()
    private var renderPass: Long = VK_NULL_HANDLE
    private var graphicsPipeline: Long = VK_NULL_HANDLE
    private var whiteTexture: TextureResource? = null
    private var fontTexture: TextureResource? = null
    private var vertexBuffer: BufferAllocation? = null
    private var previousAdvance: Boolean = false
    private var previousChoiceKeys: List<Boolean> = List(4) { false }
    private var previousMousePressed: Boolean = false
    private var viewportX: Int = 0
    private var viewportY: Int = 0
    private var viewportWidth: Int = config.width
    private var viewportHeight: Int = config.height
    private var sceneWidth: Float = config.width.toFloat()
    private var sceneHeight: Float = config.height.toFloat()
    private var closed: Boolean = false
    private var pendingCapture: Path? = null

    init {
        check(glfwInit()) { "failed to initialize glfw" }
        check(glfwVulkanSupported()) { "vulkan is not available on this system" }
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API)
        glfwWindowHint(GLFW_RESIZABLE, if (config.resizable) GLFW_TRUE else 0)
        glfwWindowHint(GLFW_VISIBLE, 0)
        window = glfwCreateWindow(config.width, config.height, config.title, 0L, 0L)
        if (window == 0L) {
            glfwTerminate()
            error("failed to create the akt window")
        }

        instance = createInstance()
        surface = createSurface(instance, window)
        physicalDevice = choosePhysicalDevice(instance)
        val queueFamilies = findQueueFamilies(physicalDevice)
        device = createLogicalDevice(physicalDevice, queueFamilies)
        graphicsQueue = getQueue(device, queueFamilies.graphics)
        presentQueue = getQueue(device, queueFamilies.present)
        sampler = createSampler(device)
        descriptorSetLayout = createDescriptorSetLayout(device)
        descriptorPool = createDescriptorPool(device)
        pipelineLayout = createPipelineLayout(device, descriptorSetLayout)
        commandPool = createCommandPool(device, queueFamilies.graphics)
        commandBuffer = allocateCommandBuffer(device, commandPool)

        val sync = createSyncObjects(device)
        imageAvailableSemaphore = sync.first
        inFlightFence = sync.second

        createSwapchain()
        createRenderPass()
        createFramebuffers()
        createGraphicsPipeline()

        fontAtlas = FontAtlas()
        whiteTexture = createTexture(1, 1, rgbaWhitePixel())
        fontTexture = createTexture(fontAtlas.width, fontAtlas.height, fontAtlas.pixels)

        glfwShowWindow(window)
    }

    /** returns true when the window or renderer has closed. */
    override fun shouldClose(): Boolean = closed || glfwWindowShouldClose(window)

    /** polls edge-triggered keyboard and pointer input. */
    override fun pollInput(): InputFrame {
        if (shouldClose()) return InputFrame(closeRequested = true)
        glfwPollEvents()

        val advanceKeys =
            listOf(
                glfwGetKey(window, GLFW_KEY_ENTER) == GLFW_PRESS,
                glfwGetKey(window, GLFW_KEY_KP_ENTER) == GLFW_PRESS,
                glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS,
            )
        val advance = advanceKeys.any { it } && !previousAdvance
        previousAdvance = advanceKeys.any { it }

        val choiceKeys =
            listOf(
                glfwGetKey(window, GLFW_KEY_1) == GLFW_PRESS,
                glfwGetKey(window, GLFW_KEY_2) == GLFW_PRESS,
                glfwGetKey(window, GLFW_KEY_3) == GLFW_PRESS,
                glfwGetKey(window, GLFW_KEY_4) == GLFW_PRESS,
            )
        val choiceIndex =
            choiceKeys
                .withIndex()
                .firstOrNull { (index, pressed) -> pressed && !previousChoiceKeys[index] }
                ?.index
        previousChoiceKeys = choiceKeys

        val closeRequested = glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS
        val mousePressed = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS
        val click = mousePressed && !previousMousePressed
        previousMousePressed = mousePressed
        val pointer = if (click) pointerPosition() else null

        return InputFrame(
            advance = advance,
            choiceIndex = choiceIndex,
            pointer = pointer,
            closeRequested = closeRequested,
        )
    }

    /** builds draw batches and presents one vulkan frame. */
    override fun render(scene: UiScene) {
        if (closed || scene.width <= 0f || scene.height <= 0f) return
        sceneWidth = scene.width
        sceneHeight = scene.height

        stackPush().use { stack ->
            val framebufferWidth = stack.mallocInt(1)
            val framebufferHeight = stack.mallocInt(1)
            glfwGetFramebufferSize(window, framebufferWidth, framebufferHeight)
            val width = framebufferWidth.get(0)
            val height = framebufferHeight.get(0)
            if (width <= 0 || height <= 0) return
            if (width != swapchainExtentWidth || height != swapchainExtentHeight) {
                recreateSwapchain()
            }
        }

        updateViewport()
        val batches = buildBatches(scene)
        val capturePath = pendingCapture

        val acquire =
            stackPush().use { acquireStack ->
                checkVk(
                    vkWaitForFences(
                        device,
                        acquireStack.longs(inFlightFence),
                        true,
                        Long.MAX_VALUE,
                    ),
                    "failed to wait for the vulkan frame fence",
                )
                val imageIndexBuffer = acquireStack.mallocInt(1)
                val result =
                    vkAcquireNextImageKHR(
                        device,
                        swapchain,
                        Long.MAX_VALUE,
                        imageAvailableSemaphore,
                        VK_NULL_HANDLE,
                        imageIndexBuffer,
                    )
                result to imageIndexBuffer.get(0)
            }
        val acquireResult = acquire.first
        val imageIndex = acquire.second
        if (acquireResult == VK_ERROR_OUT_OF_DATE_KHR) {
            recreateSwapchain()
            return
        }
        if (acquireResult != VK_SUBOPTIMAL_KHR && acquireResult != VK_SUCCESS) {
            error("failed to acquire a vulkan swapchain image (vkresult=$acquireResult)")
        }

        stackPush().use { fenceStack -> vkResetFences(device, fenceStack.longs(inFlightFence)) }
        vkResetCommandBuffer(commandBuffer, 0)
        val captureBuffer =
            if (capturePath != null) {
                createReadbackBuffer(swapchainExtentWidth, swapchainExtentHeight)
            } else {
                null
            }
        recordFrame(scene, batches, imageIndex, captureBuffer)
        submitFrame(imageIndex)

        if (capturePath != null && captureBuffer != null) {
            stackPush().use { captureStack ->
                checkVk(
                    vkWaitForFences(
                        device,
                        captureStack.longs(inFlightFence),
                        true,
                        Long.MAX_VALUE,
                    ),
                    "failed to wait for the vulkan capture fence",
                )
            }
            writeCapture(captureBuffer, capturePath)
            destroyBuffer(captureBuffer)
            pendingCapture = null
        }

        val presentResult = present(imageIndex)
        if (
            presentResult == VK_ERROR_OUT_OF_DATE_KHR ||
                presentResult == VK_SUBOPTIMAL_KHR ||
                acquireResult == VK_SUBOPTIMAL_KHR
        ) {
            recreateSwapchain()
        } else {
            checkVk(presentResult, "failed to present a vulkan frame")
        }
    }

    /** requests a png capture of the next successfully rendered frame. */
    override fun requestCapture(path: Path) {
        pendingCapture = path.toAbsolutePath().normalize()
    }

    /** returns the glfw monotonic clock in seconds. */
    override fun elapsedSeconds(): Double = glfwGetTime()

    /** waits for the device and releases all renderer resources. */
    override fun close() {
        if (closed) return
        vkDeviceWaitIdle(device)

        textures.values.forEach { texture -> texture?.let(::destroyTexture) }
        textures.clear()
        whiteTexture?.let(::destroyTexture)
        fontTexture?.let(::destroyTexture)
        whiteTexture = null
        fontTexture = null

        vertexBuffer?.let(::destroyBuffer)
        vertexBuffer = null

        destroySwapchainObjects()
        if (graphicsPipeline != VK_NULL_HANDLE) {
            vkDestroyPipeline(device, graphicsPipeline, null)
        }
        if (renderPass != VK_NULL_HANDLE) {
            vkDestroyRenderPass(device, renderPass, null)
        }
        vkDestroyPipelineLayout(device, pipelineLayout, null)
        vkDestroyDescriptorPool(device, descriptorPool, null)
        vkDestroyDescriptorSetLayout(device, descriptorSetLayout, null)
        vkDestroySampler(device, sampler, null)
        vkDestroySemaphore(device, imageAvailableSemaphore, null)
        vkDestroyFence(device, inFlightFence, null)
        vkDestroyCommandPool(device, commandPool, null)
        vkDestroyDevice(device, null)
        vkDestroySurfaceKHR(instance, surface, null)
        vkDestroyInstance(instance, null)
        glfwDestroyWindow(window)
        glfwTerminate()
        closed = true
    }

    private fun createInstance(): VkInstance =
        stackPush().use { stack ->
            val required =
                glfwGetRequiredInstanceExtensions()
                    ?: error("glfw could not provide vulkan instance extensions")
            val extensionNames = mutableListOf<String>()
            for (index in 0 until required.remaining()) {
                extensionNames += memUTF8(required.get(index))
            }
            val available = enumerateInstanceExtensions()
            val portability = isMac() && VK_KHR_PORTABILITY_ENUMERATION_EXTENSION_NAME in available
            if (portability) {
                extensionNames += VK_KHR_PORTABILITY_ENUMERATION_EXTENSION_NAME
            }

            val extensions = stack.mallocPointer(extensionNames.size)
            extensionNames.forEach { name -> extensions.put(stack.UTF8(name)) }
            extensions.flip()

            val applicationInfo =
                VkApplicationInfo.calloc(stack)
                    .`sType$Default`()
                    .pApplicationName(stack.UTF8("akt"))
                    .applicationVersion(1)
                    .pEngineName(stack.UTF8("akt"))
                    .engineVersion(1)
                    .apiVersion(VK_API_VERSION_1_0)
            val createInfo =
                VkInstanceCreateInfo.calloc(stack)
                    .`sType$Default`()
                    .flags(
                        if (portability) {
                            VK_INSTANCE_CREATE_ENUMERATE_PORTABILITY_BIT_KHR
                        } else {
                            0
                        }
                    )
                    .pApplicationInfo(applicationInfo)
                    .ppEnabledExtensionNames(extensions)
            val instanceHandle = stack.mallocPointer(1)
            checkVk(
                vkCreateInstance(createInfo, null, instanceHandle),
                "failed to create the vulkan instance",
            )
            VkInstance(instanceHandle.get(0), createInfo)
        }

    private fun createSurface(instance: VkInstance, window: Long): Long =
        stackPush().use { stack ->
            val surfaceHandle = stack.mallocLong(1)
            checkVk(
                glfwCreateWindowSurface(instance, window, null, surfaceHandle),
                "failed to create the vulkan surface",
            )
            surfaceHandle.get(0)
        }

    private fun choosePhysicalDevice(instance: VkInstance): VkPhysicalDevice =
        stackPush().use { stack ->
            val count = stack.mallocInt(1)
            checkVk(
                vkEnumeratePhysicalDevices(instance, count, null),
                "failed to enumerate vulkan devices",
            )
            check(count.get(0) > 0) { "no vulkan devices are available" }
            val devices = stack.mallocPointer(count.get(0))
            checkVk(
                vkEnumeratePhysicalDevices(instance, count, devices),
                "failed to read vulkan devices",
            )

            val candidates =
                (0 until count.get(0))
                    .map { VkPhysicalDevice(devices.get(it), instance) }
                    .filter(::isDeviceSuitable)
            check(candidates.isNotEmpty()) { "no vulkan device supports presentation" }

            candidates.maxByOrNull { candidate ->
                val properties = VkPhysicalDeviceProperties.malloc(stack)
                vkGetPhysicalDeviceProperties(candidate, properties)
                when (properties.deviceType()) {
                    VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU -> 3
                    VK_PHYSICAL_DEVICE_TYPE_INTEGRATED_GPU -> 2
                    else -> 1
                }
            } ?: error("no suitable vulkan device is available")
        }

    private fun createLogicalDevice(
        physicalDevice: VkPhysicalDevice,
        queueFamilies: QueueFamilies,
    ): VkDevice =
        stackPush().use { stack ->
            val uniqueFamilies = queueFamilies.run { listOf(graphics, present).distinct() }
            val priorities = stack.floats(1f)
            val queueCreateInfos = VkDeviceQueueCreateInfo.calloc(uniqueFamilies.size, stack)
            uniqueFamilies.forEachIndexed { index, family ->
                queueCreateInfos[index]
                    .`sType$Default`()
                    .queueFamilyIndex(family)
                    .pQueuePriorities(priorities)
            }

            val availableExtensions = enumerateDeviceExtensions(physicalDevice)
            val extensionNames = mutableListOf(VK_KHR_SWAPCHAIN_EXTENSION_NAME)
            if (isMac() && VK_KHR_PORTABILITY_SUBSET_EXTENSION_NAME in availableExtensions) {
                extensionNames += VK_KHR_PORTABILITY_SUBSET_EXTENSION_NAME
            }
            val extensions = stack.mallocPointer(extensionNames.size)
            extensionNames.forEach { name -> extensions.put(stack.UTF8(name)) }
            extensions.flip()

            val features = VkPhysicalDeviceFeatures.calloc(stack)
            vkGetPhysicalDeviceFeatures(physicalDevice, features)

            val createInfo =
                VkDeviceCreateInfo.calloc(stack)
                    .`sType$Default`()
                    .pQueueCreateInfos(queueCreateInfos)
                    .ppEnabledExtensionNames(extensions)
                    .pEnabledFeatures(features)
            val deviceHandle = stack.mallocPointer(1)
            checkVk(
                vkCreateDevice(physicalDevice, createInfo, null, deviceHandle),
                "failed to create the vulkan device",
            )
            VkDevice(deviceHandle.get(0), physicalDevice, createInfo)
        }

    private fun createSampler(device: VkDevice): Long =
        stackPush().use { stack ->
            val createInfo =
                VkSamplerCreateInfo.calloc(stack)
                    .`sType$Default`()
                    .magFilter(VK_FILTER_LINEAR)
                    .minFilter(VK_FILTER_LINEAR)
                    .mipmapMode(VK_SAMPLER_MIPMAP_MODE_LINEAR)
                    .addressModeU(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
                    .addressModeV(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
                    .addressModeW(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
                    .anisotropyEnable(false)
                    .compareEnable(false)
                    .borderColor(VK_BORDER_COLOR_FLOAT_OPAQUE_BLACK)
                    .unnormalizedCoordinates(false)
            val samplerHandle = stack.mallocLong(1)
            checkVk(
                vkCreateSampler(device, createInfo, null, samplerHandle),
                "failed to create the vulkan sampler",
            )
            samplerHandle.get(0)
        }

    private fun createDescriptorSetLayout(device: VkDevice): Long =
        stackPush().use { stack ->
            val binding =
                VkDescriptorSetLayoutBinding.calloc(1, stack)
                    .binding(0)
                    .descriptorType(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER)
                    .descriptorCount(1)
                    .stageFlags(VK_SHADER_STAGE_FRAGMENT_BIT)
            val createInfo =
                VkDescriptorSetLayoutCreateInfo.calloc(stack).`sType$Default`().pBindings(binding)
            val layout = stack.mallocLong(1)
            checkVk(
                vkCreateDescriptorSetLayout(device, createInfo, null, layout),
                "failed to create descriptor set layout",
            )
            layout.get(0)
        }

    private fun createDescriptorPool(device: VkDevice): Long =
        stackPush().use { stack ->
            val poolSize =
                VkDescriptorPoolSize.calloc(1, stack)
                    .type(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER)
                    .descriptorCount(128)
            val createInfo =
                VkDescriptorPoolCreateInfo.calloc(stack)
                    .`sType$Default`()
                    .maxSets(128)
                    .pPoolSizes(poolSize)
            val pool = stack.mallocLong(1)
            checkVk(
                vkCreateDescriptorPool(device, createInfo, null, pool),
                "failed to create descriptor pool",
            )
            pool.get(0)
        }

    private fun createPipelineLayout(device: VkDevice, setLayout: Long): Long =
        stackPush().use { stack ->
            val pushRange =
                VkPushConstantRange.calloc(1, stack)
                    .stageFlags(VK_SHADER_STAGE_VERTEX_BIT)
                    .offset(0)
                    .size(8)
            val createInfo =
                VkPipelineLayoutCreateInfo.calloc(stack)
                    .`sType$Default`()
                    .pSetLayouts(stack.longs(setLayout))
                    .pPushConstantRanges(pushRange)
            val layout = stack.mallocLong(1)
            checkVk(
                vkCreatePipelineLayout(device, createInfo, null, layout),
                "failed to create pipeline layout",
            )
            layout.get(0)
        }

    private fun createCommandPool(device: VkDevice, graphicsFamily: Int): Long =
        stackPush().use { stack ->
            val createInfo =
                VkCommandPoolCreateInfo.calloc(stack)
                    .`sType$Default`()
                    .flags(VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT)
                    .queueFamilyIndex(graphicsFamily)
            val pool = stack.mallocLong(1)
            checkVk(
                vkCreateCommandPool(device, createInfo, null, pool),
                "failed to create command pool",
            )
            pool.get(0)
        }

    private fun allocateCommandBuffer(
        device: VkDevice,
        commandPool: Long,
    ): VkCommandBuffer =
        stackPush().use { stack ->
            val allocateInfo =
                VkCommandBufferAllocateInfo.calloc(stack)
                    .`sType$Default`()
                    .commandPool(commandPool)
                    .level(VK_COMMAND_BUFFER_LEVEL_PRIMARY)
                    .commandBufferCount(1)
            val commandBuffers = stack.mallocPointer(1)
            checkVk(
                vkAllocateCommandBuffers(device, allocateInfo, commandBuffers),
                "failed to allocate command buffer",
            )
            VkCommandBuffer(commandBuffers.get(0), device)
        }

    private fun createSyncObjects(device: VkDevice): Pair<Long, Long> =
        stackPush().use { stack ->
            val semaphoreInfo = VkSemaphoreCreateInfo.calloc(stack).`sType$Default`()
            val fenceInfo =
                VkFenceCreateInfo.calloc(stack)
                    .`sType$Default`()
                    .flags(VK_FENCE_CREATE_SIGNALED_BIT)
            val imageAvailable = stack.mallocLong(1)
            val fence = stack.mallocLong(1)
            checkVk(
                vkCreateSemaphore(device, semaphoreInfo, null, imageAvailable),
                "failed to create image semaphore",
            )
            checkVk(
                vkCreateFence(device, fenceInfo, null, fence),
                "failed to create frame fence",
            )
            imageAvailable.get(0) to fence.get(0)
        }

    private fun createSwapchain() =
        stackPush().use { stack ->
            val support = querySwapchainSupport(physicalDevice)
            val format = chooseSurfaceFormat(support.formats)
            val presentMode = choosePresentMode(support.presentModes)
            val extent = chooseExtent(support, stack)
            var imageCount = support.minImageCount + 1
            if (support.maxImageCount > 0 && imageCount > support.maxImageCount) {
                imageCount = support.maxImageCount
            }
            val usage = VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT or VK_IMAGE_USAGE_TRANSFER_SRC_BIT
            require(support.supportedUsageFlags and usage == usage) {
                "vulkan swapchain does not support transfer readback"
            }

            val compositeAlpha = chooseCompositeAlpha(support)
            val createInfo =
                VkSwapchainCreateInfoKHR.calloc(stack)
                    .`sType$Default`()
                    .surface(surface)
                    .minImageCount(imageCount)
                    .imageFormat(format.format)
                    .imageColorSpace(format.colorSpace)
                    .imageExtent(extent)
                    .imageArrayLayers(1)
                    .imageUsage(usage)
                    .imageSharingMode(VK_SHARING_MODE_EXCLUSIVE)
                    .preTransform(chooseSurfaceTransform(support))
                    .compositeAlpha(compositeAlpha)
                    .presentMode(presentMode)
                    .clipped(true)
                    .oldSwapchain(VK_NULL_HANDLE)

            val swapchainHandle = stack.mallocLong(1)
            checkVk(
                vkCreateSwapchainKHR(device, createInfo, null, swapchainHandle),
                "failed to create vulkan swapchain",
            )
            swapchain = swapchainHandle.get(0)
            swapchainFormat = format.format
            swapchainExtentWidth = extent.width()
            swapchainExtentHeight = extent.height()

            val imageCountBuffer = stack.mallocInt(1)
            checkVk(
                vkGetSwapchainImagesKHR(device, swapchain, imageCountBuffer, null),
                "failed to count swapchain images",
            )
            val images = stack.mallocLong(imageCountBuffer.get(0))
            checkVk(
                vkGetSwapchainImagesKHR(device, swapchain, imageCountBuffer, images),
                "failed to read swapchain images",
            )
            swapchainImages = List(imageCountBuffer.get(0)) { images.get(it) }
            renderFinishedSemaphores = swapchainImages.mapTo(mutableListOf()) { createSemaphore() }

            swapchainImageViews =
                swapchainImages.mapTo(mutableListOf()) { image ->
                    createImageView(device, image, swapchainFormat)
                }
        }

    private fun createRenderPass() =
        stackPush().use { stack ->
            val colorAttachment =
                VkAttachmentDescription.calloc(1, stack)
                    .format(swapchainFormat)
                    .samples(VK_SAMPLE_COUNT_1_BIT)
                    .loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR)
                    .storeOp(VK_ATTACHMENT_STORE_OP_STORE)
                    .stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
                    .stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
                    .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
                    .finalLayout(VK_IMAGE_LAYOUT_PRESENT_SRC_KHR)
            val colorReference =
                VkAttachmentReference.calloc(1, stack)
                    .attachment(0)
                    .layout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
            val subpass =
                VkSubpassDescription.calloc(1, stack)
                    .pipelineBindPoint(VK_PIPELINE_BIND_POINT_GRAPHICS)
                    .colorAttachmentCount(1)
                    .pColorAttachments(colorReference)
            val dependency =
                VkSubpassDependency.calloc(1, stack)
                    .srcSubpass(VK_SUBPASS_EXTERNAL)
                    .dstSubpass(0)
                    .srcStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT)
                    .dstStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT)
                    .srcAccessMask(0)
                    .dstAccessMask(VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT)
            val createInfo =
                VkRenderPassCreateInfo.calloc(stack)
                    .`sType$Default`()
                    .pAttachments(colorAttachment)
                    .pSubpasses(subpass)
                    .pDependencies(dependency)
            val renderPassHandle = stack.mallocLong(1)
            checkVk(
                vkCreateRenderPass(device, createInfo, null, renderPassHandle),
                "failed to create render pass",
            )
            renderPass = renderPassHandle.get(0)
        }

    private fun createGraphicsPipeline() =
        stackPush().use { stack ->
            val vertexShader = loadShader("ui.vert.spv")
            val fragmentShader = loadShader("ui.frag.spv")
            val vertexModule = createShaderModule(device, vertexShader)
            val fragmentModule = createShaderModule(device, fragmentShader)
            try {
                val stages = VkPipelineShaderStageCreateInfo.calloc(2, stack)
                stages[0]
                    .`sType$Default`()
                    .stage(VK_SHADER_STAGE_VERTEX_BIT)
                    .module(vertexModule)
                    .pName(stack.UTF8("main"))
                stages[1]
                    .`sType$Default`()
                    .stage(VK_SHADER_STAGE_FRAGMENT_BIT)
                    .module(fragmentModule)
                    .pName(stack.UTF8("main"))

                val binding =
                    VkVertexInputBindingDescription.calloc(1, stack)
                        .binding(0)
                        .stride(32)
                        .inputRate(VK_VERTEX_INPUT_RATE_VERTEX)
                val attributes = VkVertexInputAttributeDescription.calloc(3, stack)
                attributes[0].location(0).binding(0).format(VK_FORMAT_R32G32_SFLOAT).offset(0)
                attributes[1].location(1).binding(0).format(VK_FORMAT_R32G32_SFLOAT).offset(8)
                attributes[2]
                    .location(2)
                    .binding(0)
                    .format(VK_FORMAT_R32G32B32A32_SFLOAT)
                    .offset(16)
                val vertexInput =
                    VkPipelineVertexInputStateCreateInfo.calloc(stack)
                        .`sType$Default`()
                        .pVertexBindingDescriptions(binding)
                        .pVertexAttributeDescriptions(attributes)

                val inputAssembly =
                    VkPipelineInputAssemblyStateCreateInfo.calloc(stack)
                        .`sType$Default`()
                        .topology(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST)
                        .primitiveRestartEnable(false)
                val viewportState =
                    VkPipelineViewportStateCreateInfo.calloc(stack)
                        .`sType$Default`()
                        .viewportCount(1)
                        .scissorCount(1)
                val rasterization =
                    VkPipelineRasterizationStateCreateInfo.calloc(stack)
                        .`sType$Default`()
                        .depthClampEnable(false)
                        .rasterizerDiscardEnable(false)
                        .polygonMode(VK_POLYGON_MODE_FILL)
                        .cullMode(VK_CULL_MODE_NONE)
                        .frontFace(VK_FRONT_FACE_COUNTER_CLOCKWISE)
                        .depthBiasEnable(false)
                        .lineWidth(1f)
                val multisample =
                    VkPipelineMultisampleStateCreateInfo.calloc(stack)
                        .`sType$Default`()
                        .rasterizationSamples(VK_SAMPLE_COUNT_1_BIT)
                        .sampleShadingEnable(false)
                val blendAttachment =
                    VkPipelineColorBlendAttachmentState.calloc(1, stack)
                        .blendEnable(true)
                        .srcColorBlendFactor(VK_BLEND_FACTOR_SRC_ALPHA)
                        .dstColorBlendFactor(VK_BLEND_FACTOR_ONE_MINUS_SRC_ALPHA)
                        .colorBlendOp(VK_BLEND_OP_ADD)
                        .srcAlphaBlendFactor(VK_BLEND_FACTOR_ONE)
                        .dstAlphaBlendFactor(VK_BLEND_FACTOR_ONE_MINUS_SRC_ALPHA)
                        .alphaBlendOp(VK_BLEND_OP_ADD)
                        .colorWriteMask(
                            VK_COLOR_COMPONENT_R_BIT or
                                VK_COLOR_COMPONENT_G_BIT or
                                VK_COLOR_COMPONENT_B_BIT or
                                VK_COLOR_COMPONENT_A_BIT
                        )
                val colorBlend =
                    VkPipelineColorBlendStateCreateInfo.calloc(stack)
                        .`sType$Default`()
                        .logicOpEnable(false)
                        .pAttachments(blendAttachment)
                val dynamicStates =
                    stack
                        .mallocInt(2)
                        .put(0, VK_DYNAMIC_STATE_VIEWPORT)
                        .put(1, VK_DYNAMIC_STATE_SCISSOR)
                val dynamicState =
                    VkPipelineDynamicStateCreateInfo.calloc(stack)
                        .`sType$Default`()
                        .pDynamicStates(dynamicStates)
                val pipelineInfo = VkGraphicsPipelineCreateInfo.calloc(1, stack)
                pipelineInfo[0]
                    .`sType$Default`()
                    .pStages(stages)
                    .pVertexInputState(vertexInput)
                    .pInputAssemblyState(inputAssembly)
                    .pViewportState(viewportState)
                    .pRasterizationState(rasterization)
                    .pMultisampleState(multisample)
                    .pColorBlendState(colorBlend)
                    .pDynamicState(dynamicState)
                    .layout(pipelineLayout)
                    .renderPass(renderPass)
                    .subpass(0)
                    .basePipelineHandle(VK_NULL_HANDLE)
                    .basePipelineIndex(-1)
                val pipelineHandle = stack.mallocLong(1)
                checkVk(
                    vkCreateGraphicsPipelines(
                        device,
                        VK_NULL_HANDLE,
                        pipelineInfo,
                        null,
                        pipelineHandle,
                    ),
                    "failed to create graphics pipeline",
                )
                graphicsPipeline = pipelineHandle.get(0)
            } finally {
                vkDestroyShaderModule(device, vertexModule, null)
                vkDestroyShaderModule(device, fragmentModule, null)
                memFree(vertexShader)
                memFree(fragmentShader)
            }
        }

    private fun recordFrame(
        scene: UiScene,
        batches: List<DrawBatch>,
        imageIndex: Int,
        captureBuffer: BufferAllocation?,
    ) =
        stackPush().use { stack ->
            val beginInfo = VkCommandBufferBeginInfo.calloc(stack).`sType$Default`()
            checkVk(
                vkBeginCommandBuffer(commandBuffer, beginInfo),
                "failed to begin command buffer",
            )

            val clearValue = VkClearValue.calloc(1, stack)
            clearValue[0]
                .color()
                .float32(
                    0,
                    scene.clearColor.red,
                )
                .float32(1, scene.clearColor.green)
                .float32(2, scene.clearColor.blue)
                .float32(3, 1f)
            val renderInfo =
                VkRenderPassBeginInfo.calloc(stack)
                    .`sType$Default`()
                    .renderPass(renderPass)
                    .framebuffer(framebuffers[imageIndex])
                    .renderArea {
                        it.offset { offset -> offset.set(0, 0) }
                        it.extent { extent ->
                            extent.width(swapchainExtentWidth)
                            extent.height(swapchainExtentHeight)
                        }
                    }
                    .pClearValues(clearValue)
            vkCmdBeginRenderPass(commandBuffer, renderInfo, VK_SUBPASS_CONTENTS_INLINE)

            val viewport = VkViewport.calloc(1, stack)
            viewport[0]
                .x(viewportX.toFloat())
                .y(viewportY.toFloat())
                .width(viewportWidth.toFloat())
                .height(viewportHeight.toFloat())
                .minDepth(0f)
                .maxDepth(1f)
            vkCmdSetViewport(commandBuffer, 0, viewport)
            val scissors = VkRect2D.calloc(1, stack)
            scissors[0]
                .offset { offset -> offset.set(viewportX, viewportY) }
                .extent { extent ->
                    extent.width(viewportWidth)
                    extent.height(viewportHeight)
                }
            vkCmdSetScissor(commandBuffer, 0, scissors)

            vkCmdBindPipeline(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, graphicsPipeline)
            vkCmdPushConstants(
                commandBuffer,
                pipelineLayout,
                VK_SHADER_STAGE_VERTEX_BIT,
                0,
                stack.floats(scene.width, scene.height),
            )

            val vertexData = flattenBatches(batches)
            if (vertexData.isNotEmpty()) {
                val allocation = ensureVertexBuffer(vertexData.size * Float.SIZE_BYTES)
                writeVertexData(allocation, vertexData)
                vkCmdBindVertexBuffers(
                    commandBuffer,
                    0,
                    stack.longs(allocation.buffer),
                    stack.longs(0L),
                )
                var firstVertex = 0
                batches.forEach { batch ->
                    vkCmdBindDescriptorSets(
                        commandBuffer,
                        VK_PIPELINE_BIND_POINT_GRAPHICS,
                        pipelineLayout,
                        0,
                        stack.longs(batch.texture.descriptorSet),
                        null,
                    )
                    val vertexCount = batch.vertices.size / VERTEX_FLOAT_COUNT
                    vkCmdDraw(commandBuffer, vertexCount, 1, firstVertex, 0)
                    firstVertex += vertexCount
                }
            }
            vkCmdEndRenderPass(commandBuffer)

            if (captureBuffer != null) {
                val imageBarrier = VkImageMemoryBarrier.calloc(1, stack)
                imageBarrier[0]
                    .`sType$Default`()
                    .srcAccessMask(VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT)
                    .dstAccessMask(VK_ACCESS_TRANSFER_READ_BIT)
                    .oldLayout(VK_IMAGE_LAYOUT_PRESENT_SRC_KHR)
                    .newLayout(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)
                    .srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                    .dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                    .image(swapchainImages[imageIndex])
                    .subresourceRange { range ->
                        range.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                        range.baseMipLevel(0)
                        range.levelCount(1)
                        range.baseArrayLayer(0)
                        range.layerCount(1)
                    }
                vkCmdPipelineBarrier(
                    commandBuffer,
                    VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT,
                    VK_PIPELINE_STAGE_TRANSFER_BIT,
                    0,
                    null,
                    null,
                    imageBarrier,
                )

                val copy = VkBufferImageCopy.calloc(1, stack)
                copy[0]
                    .bufferOffset(0)
                    .bufferRowLength(0)
                    .bufferImageHeight(0)
                    .imageSubresource { subresource ->
                        subresource.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                        subresource.mipLevel(0)
                        subresource.baseArrayLayer(0)
                        subresource.layerCount(1)
                    }
                    .imageOffset { offset -> offset.set(0, 0, 0) }
                    .imageExtent { extent ->
                        extent.set(swapchainExtentWidth, swapchainExtentHeight, 1)
                    }
                vkCmdCopyImageToBuffer(
                    commandBuffer,
                    swapchainImages[imageIndex],
                    VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL,
                    captureBuffer.buffer,
                    copy,
                )

                imageBarrier[0]
                    .srcAccessMask(VK_ACCESS_TRANSFER_READ_BIT)
                    .dstAccessMask(0)
                    .oldLayout(VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL)
                    .newLayout(VK_IMAGE_LAYOUT_PRESENT_SRC_KHR)
                vkCmdPipelineBarrier(
                    commandBuffer,
                    VK_PIPELINE_STAGE_TRANSFER_BIT,
                    VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT,
                    0,
                    null,
                    null,
                    imageBarrier,
                )
            }

            checkVk(vkEndCommandBuffer(commandBuffer), "failed to end command buffer")
        }

    private fun submitFrame(imageIndex: Int) =
        stackPush().use { stack ->
            val waitStages = stack.mallocInt(1)
            waitStages.put(0, VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT)
            val submitInfo =
                VkSubmitInfo.calloc(stack)
                    .`sType$Default`()
                    .waitSemaphoreCount(1)
                    .pWaitSemaphores(stack.longs(imageAvailableSemaphore))
                    .pWaitDstStageMask(waitStages)
                    .pCommandBuffers(stack.pointers(commandBuffer))
                    .pSignalSemaphores(stack.longs(renderFinishedSemaphores[imageIndex]))
            checkVk(
                vkQueueSubmit(graphicsQueue, submitInfo, inFlightFence),
                "failed to submit vulkan frame",
            )
        }

    private fun present(imageIndex: Int): Int =
        stackPush().use { stack ->
            val presentInfo =
                VkPresentInfoKHR.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_PRESENT_INFO_KHR)
                    .pWaitSemaphores(stack.longs(renderFinishedSemaphores[imageIndex]))
                    .swapchainCount(1)
                    .pSwapchains(stack.longs(swapchain))
                    .pImageIndices(stack.ints(imageIndex))
            vkQueuePresentKHR(presentQueue, presentInfo)
        }

    private fun buildBatches(scene: UiScene): List<DrawBatch> {
        val batches = mutableListOf<DrawBatch>()
        scene.items.sortedBy(UiItem::layer).forEach { item ->
            when (item) {
                is PanelItem ->
                    appendPanel(
                        batches,
                        requireWhiteTexture(),
                        item.bounds,
                        item.color,
                        item.transform,
                    )
                is ImageItem -> appendImage(batches, item)
                is TextItem -> appendText(batches, item)
                is ChoiceItem -> {
                    appendPanel(
                        batches,
                        requireWhiteTexture(),
                        item.bounds,
                        item.color,
                        item.transform,
                    )
                    appendText(
                        batches,
                        TextItem(
                            id = "${item.id}-text",
                            bounds =
                                Rect(
                                    item.bounds.x + 22f,
                                    item.bounds.y + 9f,
                                    item.bounds.width - 44f,
                                    item.bounds.height - 12f,
                                ),
                            layer = item.layer + 1,
                            transform = item.transform,
                            text = item.text,
                            style = item.textStyle,
                        ),
                    )
                }
            }
        }
        return batches.filter { it.vertices.isNotEmpty() }
    }

    private fun appendImage(batches: MutableList<DrawBatch>, item: ImageItem) {
        val texture = textureFor(item.asset)
        if (texture == null) {
            val base = fallbackColor(item.asset)
            appendPanel(
                batches,
                requireWhiteTexture(),
                item.bounds,
                base,
                item.transform,
            )
            appendPanel(
                batches,
                requireWhiteTexture(),
                Rect(
                    item.bounds.x,
                    item.bounds.y + item.bounds.height * 0.68f,
                    item.bounds.width,
                    item.bounds.height * 0.32f,
                ),
                base.withAlpha(0.46f),
                item.transform,
            )
        } else {
            appendTexturedQuad(
                batches,
                texture,
                item.bounds,
                Uv(0f, 0f, 1f, 1f),
                item.tint,
                item.transform,
            )
        }
    }

    private fun appendText(batches: MutableList<DrawBatch>, item: TextItem) {
        if (item.text.isEmpty()) return
        val texture = fontTexture ?: return
        val lines = fontAtlas.wrap(item.text, item.bounds.width, item.style.size)
        val scale = item.style.size / 42f
        val lineStep = item.style.size * item.style.lineHeight
        lines.forEachIndexed { lineIndex, line ->
            val lineWidth = fontAtlas.measure(line, item.style.size)
            val startX =
                when (item.style.align) {
                    TextAlign.LEFT -> item.bounds.x
                    TextAlign.CENTER -> item.bounds.x + (item.bounds.width - lineWidth) / 2f
                    TextAlign.RIGHT -> item.bounds.x + item.bounds.width - lineWidth
                }
            var x = startX
            val y = item.bounds.y + lineIndex * lineStep
            line.forEach { character ->
                val glyph = fontAtlas.glyph(character)
                appendTexturedQuad(
                    batches,
                    texture,
                    Rect(
                        x,
                        y,
                        fontAtlas.cellWidth(item.style.size),
                        fontAtlas.cellHeight(item.style.size),
                    ),
                    Uv(glyph.u0, glyph.v0, glyph.u1, glyph.v1),
                    item.style.color,
                    item.transform,
                )
                x += glyph.advance * scale
            }
        }
    }

    private fun appendPanel(
        batches: MutableList<DrawBatch>,
        texture: TextureResource,
        bounds: Rect,
        color: Color,
        transform: Transform,
    ) {
        appendTexturedQuad(
            batches,
            texture,
            bounds,
            Uv(0f, 0f, 1f, 1f),
            color,
            transform,
        )
    }

    private fun appendTexturedQuad(
        batches: MutableList<DrawBatch>,
        texture: TextureResource,
        bounds: Rect,
        uv: Uv,
        color: Color,
        transform: Transform,
    ) {
        val batch =
            if (batches.lastOrNull()?.texture === texture) {
                batches.last()
            } else {
                DrawBatch(texture).also(batches::add)
            }
        val topLeft = transformPoint(bounds.x, bounds.y, bounds, transform)
        val topRight = transformPoint(bounds.x + bounds.width, bounds.y, bounds, transform)
        val bottomRight =
            transformPoint(
                bounds.x + bounds.width,
                bounds.y + bounds.height,
                bounds,
                transform,
            )
        val bottomLeft = transformPoint(bounds.x, bounds.y + bounds.height, bounds, transform)
        addVertex(batch, topLeft, uv.u0, uv.v0, color)
        addVertex(batch, topRight, uv.u1, uv.v0, color)
        addVertex(batch, bottomRight, uv.u1, uv.v1, color)
        addVertex(batch, topLeft, uv.u0, uv.v0, color)
        addVertex(batch, bottomRight, uv.u1, uv.v1, color)
        addVertex(batch, bottomLeft, uv.u0, uv.v1, color)
    }

    private fun addVertex(
        batch: DrawBatch,
        point: Vec2,
        u: Float,
        v: Float,
        color: Color,
    ) {
        batch.vertices += point.x
        batch.vertices += point.y
        batch.vertices += u
        batch.vertices += v
        batch.vertices += color.red
        batch.vertices += color.green
        batch.vertices += color.blue
        batch.vertices += color.alpha
    }

    private fun transformPoint(
        x: Float,
        y: Float,
        bounds: Rect,
        transform: Transform,
    ): Vec2 {
        val originX = bounds.x + bounds.width * transform.origin.x
        val originY = bounds.y + bounds.height * transform.origin.y
        val scaledX = (x - originX) * transform.scale.x
        val scaledY = (y - originY) * transform.scale.y
        val radians = Math.toRadians(transform.rotationDegrees.toDouble())
        val cosine = cos(radians).toFloat()
        val sine = sin(radians).toFloat()
        return Vec2(
            x = originX + transform.translation.x + scaledX * cosine - scaledY * sine,
            y = originY + transform.translation.y + scaledX * sine + scaledY * cosine,
        )
    }

    private fun flattenBatches(batches: List<DrawBatch>): FloatArray {
        val size = batches.sumOf { it.vertices.size }
        val data = FloatArray(size)
        var offset = 0
        batches.forEach { batch ->
            batch.vertices.forEach { value ->
                data[offset] = value
                offset += 1
            }
        }
        return data
    }

    private fun ensureVertexBuffer(byteCount: Int): BufferAllocation {
        val current = vertexBuffer
        if (current != null && current.size >= byteCount) return current
        vkDeviceWaitIdle(device)
        current?.let(::destroyBuffer)
        val allocation =
            createBuffer(
                size = maxOf(VERTEX_BUFFER_START_SIZE, byteCount.toLong() * 2L),
                usage = VK_BUFFER_USAGE_VERTEX_BUFFER_BIT,
                properties =
                    VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT or VK_MEMORY_PROPERTY_HOST_COHERENT_BIT,
            )
        vertexBuffer = allocation
        return allocation
    }

    private fun writeVertexData(allocation: BufferAllocation, data: FloatArray) =
        stackPush().use { stack ->
            val mappedPointer = stack.mallocPointer(1)
            checkVk(
                vkMapMemory(
                    device,
                    allocation.memory,
                    0,
                    data.size.toLong() * Float.SIZE_BYTES,
                    0,
                    mappedPointer,
                ),
                "failed to map vertex memory",
            )
            val mapped =
                memByteBuffer(
                    mappedPointer.get(0),
                    data.size * Float.SIZE_BYTES,
                )
            data.forEach(mapped::putFloat)
            vkUnmapMemory(device, allocation.memory)
        }

    private fun createReadbackBuffer(width: Int, height: Int): BufferAllocation =
        createBuffer(
            size = width.toLong() * height.toLong() * 4L,
            usage = VK_BUFFER_USAGE_TRANSFER_DST_BIT,
            properties =
                VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT or VK_MEMORY_PROPERTY_HOST_COHERENT_BIT,
        )

    private fun writeCapture(buffer: BufferAllocation, path: Path) =
        stackPush().use { stack ->
            val mappedPointer = stack.mallocPointer(1)
            checkVk(
                vkMapMemory(
                    device,
                    buffer.memory,
                    0,
                    buffer.size,
                    0,
                    mappedPointer,
                ),
                "failed to map capture memory",
            )
            val pixels = memByteBuffer(mappedPointer.get(0), buffer.size.toInt())
            val width = swapchainExtentWidth
            val height = swapchainExtentHeight
            val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val offset = (y * width + x) * 4
                    val channel0 = pixels.get(offset).toInt() and 0xff
                    val channel1 = pixels.get(offset + 1).toInt() and 0xff
                    val channel2 = pixels.get(offset + 2).toInt() and 0xff
                    val red: Int
                    val green: Int
                    val blue: Int
                    when (swapchainFormat) {
                        VK_FORMAT_B8G8R8A8_SRGB,
                        VK_FORMAT_B8G8R8A8_UNORM -> {
                            blue = channel0
                            green = channel1
                            red = channel2
                        }
                        else -> {
                            red = channel0
                            green = channel1
                            blue = channel2
                        }
                    }
                    image.setRGB(x, y, (0xff shl 24) or (red shl 16) or (green shl 8) or blue)
                }
            }
            vkUnmapMemory(device, buffer.memory)
            path.parent?.let(Files::createDirectories)
            ImageIO.write(image, "png", path.toFile())
        }

    private fun textureFor(key: AssetKey): TextureResource? {
        if (textures.containsKey(key)) return textures[key]
        val bytes = assets.read(key)
        if (bytes == null) {
            textures[key] = null
            return null
        }
        val nativeBytes = memAlloc(bytes.size)
        try {
            nativeBytes.put(bytes).flip()
            stackPush().use { stack ->
                val width = stack.mallocInt(1)
                val height = stack.mallocInt(1)
                val channels = stack.mallocInt(1)
                val pixels = stbi_load_from_memory(nativeBytes, width, height, channels, 4)
                if (pixels == null) {
                    textures[key] = null
                    return null
                }
                try {
                    val texture = createTexture(width.get(0), height.get(0), pixels)
                    textures[key] = texture
                    return texture
                } finally {
                    stbi_image_free(pixels)
                }
            }
        } finally {
            memFree(nativeBytes)
        }
    }

    private fun createTexture(width: Int, height: Int, pixels: ByteBuffer): TextureResource {
        vkDeviceWaitIdle(device)
        return stackPush().use { stack ->
            val formatProperties = VkFormatProperties.calloc(stack)
            vkGetPhysicalDeviceFormatProperties(
                physicalDevice,
                VK_FORMAT_R8G8B8A8_UNORM,
                formatProperties,
            )
            require(
                formatProperties.optimalTilingFeatures() and VK_FORMAT_FEATURE_SAMPLED_IMAGE_BIT !=
                    0
            ) {
                "rgba8 textures are not supported"
            }

            val imageInfo =
                VkImageCreateInfo.calloc(stack)
                    .`sType$Default`()
                    .imageType(VK_IMAGE_TYPE_2D)
                    .format(VK_FORMAT_R8G8B8A8_UNORM)
                    .extent { extent -> extent.set(width, height, 1) }
                    .mipLevels(1)
                    .arrayLayers(1)
                    .samples(VK_SAMPLE_COUNT_1_BIT)
                    .tiling(VK_IMAGE_TILING_OPTIMAL)
                    .usage(VK_IMAGE_USAGE_TRANSFER_DST_BIT or VK_IMAGE_USAGE_SAMPLED_BIT)
                    .sharingMode(VK_SHARING_MODE_EXCLUSIVE)
                    .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
            val imageHandle = stack.mallocLong(1)
            checkVk(
                vkCreateImage(device, imageInfo, null, imageHandle),
                "failed to create texture image",
            )
            val image = imageHandle.get(0)
            val requirements = VkMemoryRequirements.malloc(stack)
            vkGetImageMemoryRequirements(device, image, requirements)
            val allocateInfo =
                VkMemoryAllocateInfo.calloc(stack)
                    .`sType$Default`()
                    .allocationSize(requirements.size())
                    .memoryTypeIndex(
                        findMemoryType(
                            requirements.memoryTypeBits(),
                            VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT,
                        )
                    )
            val memoryHandle = stack.mallocLong(1)
            checkVk(
                vkAllocateMemory(device, allocateInfo, null, memoryHandle),
                "failed to allocate texture memory",
            )
            val memory = memoryHandle.get(0)
            checkVk(
                vkBindImageMemory(device, image, memory, 0),
                "failed to bind texture memory",
            )
            val view = createImageView(device, image, VK_FORMAT_R8G8B8A8_UNORM)

            val staging =
                createBuffer(
                    size = pixels.remaining().toLong(),
                    usage = VK_BUFFER_USAGE_TRANSFER_SRC_BIT,
                    properties =
                        VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT or VK_MEMORY_PROPERTY_HOST_COHERENT_BIT,
                )
            writeBuffer(staging, pixels)
            transitionTexture(
                image,
                VK_IMAGE_LAYOUT_UNDEFINED,
                VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL,
            )
            copyBufferToImage(staging.buffer, image, width, height)
            transitionTexture(
                image,
                VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL,
                VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL,
            )
            destroyBuffer(staging)

            val descriptorSet = allocateTextureDescriptor(view)
            TextureResource(image, memory, view, descriptorSet)
        }
    }

    private fun writeBuffer(allocation: BufferAllocation, source: ByteBuffer) =
        stackPush().use { stack ->
            val mappedPointer = stack.mallocPointer(1)
            checkVk(
                vkMapMemory(
                    device,
                    allocation.memory,
                    0,
                    source.remaining().toLong(),
                    0,
                    mappedPointer,
                ),
                "failed to map staging memory",
            )
            memByteBuffer(mappedPointer.get(0), source.remaining()).put(source)
            vkUnmapMemory(device, allocation.memory)
            source.rewind()
        }

    private fun transitionTexture(image: Long, oldLayout: Int, newLayout: Int) =
        recordSingleTimeCommand { commandBuffer ->
            stackPush().use { stack ->
                val barrier = VkImageMemoryBarrier.calloc(1, stack)
                barrier[0]
                    .`sType$Default`()
                    .oldLayout(oldLayout)
                    .newLayout(newLayout)
                    .srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                    .dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                    .image(image)
                    .subresourceRange { range ->
                        range.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                        range.baseMipLevel(0)
                        range.levelCount(1)
                        range.baseArrayLayer(0)
                        range.layerCount(1)
                    }
                val sourceStage: Int
                val destinationStage: Int
                if (oldLayout == VK_IMAGE_LAYOUT_UNDEFINED) {
                    barrier[0].srcAccessMask(0).dstAccessMask(VK_ACCESS_TRANSFER_WRITE_BIT)
                    sourceStage = VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT
                    destinationStage = VK_PIPELINE_STAGE_TRANSFER_BIT
                } else {
                    barrier[0]
                        .srcAccessMask(VK_ACCESS_TRANSFER_WRITE_BIT)
                        .dstAccessMask(VK_ACCESS_SHADER_READ_BIT)
                    sourceStage = VK_PIPELINE_STAGE_TRANSFER_BIT
                    destinationStage = VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT
                }
                vkCmdPipelineBarrier(
                    commandBuffer,
                    sourceStage,
                    destinationStage,
                    0,
                    null,
                    null,
                    barrier,
                )
            }
        }

    private fun copyBufferToImage(
        buffer: Long,
        image: Long,
        width: Int,
        height: Int,
    ) = recordSingleTimeCommand { commandBuffer ->
        stackPush().use { stack ->
            val copy = VkBufferImageCopy.calloc(1, stack)
            copy[0]
                .bufferOffset(0)
                .bufferRowLength(0)
                .bufferImageHeight(0)
                .imageSubresource { subresource ->
                    subresource.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                    subresource.mipLevel(0)
                    subresource.baseArrayLayer(0)
                    subresource.layerCount(1)
                }
                .imageOffset { offset -> offset.set(0, 0, 0) }
                .imageExtent { extent -> extent.set(width, height, 1) }
            vkCmdCopyBufferToImage(
                commandBuffer,
                buffer,
                image,
                VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL,
                copy,
            )
        }
    }

    private fun recordSingleTimeCommand(block: (VkCommandBuffer) -> Unit) {
        stackPush().use { stack ->
            vkResetCommandBuffer(commandBuffer, 0)
            val beginInfo =
                VkCommandBufferBeginInfo.calloc(stack)
                    .`sType$Default`()
                    .flags(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT)
            checkVk(
                vkBeginCommandBuffer(commandBuffer, beginInfo),
                "failed to begin upload command buffer",
            )
            block(commandBuffer)
            checkVk(vkEndCommandBuffer(commandBuffer), "failed to end upload command buffer")
            val submitInfo =
                VkSubmitInfo.calloc(stack)
                    .`sType$Default`()
                    .pCommandBuffers(stack.pointers(commandBuffer))
            checkVk(
                vkQueueSubmit(graphicsQueue, submitInfo, VK_NULL_HANDLE),
                "failed to submit upload command",
            )
            checkVk(vkQueueWaitIdle(graphicsQueue), "upload queue failed")
        }
    }

    private fun allocateTextureDescriptor(imageView: Long): Long =
        stackPush().use { stack ->
            val allocateInfo =
                VkDescriptorSetAllocateInfo.calloc(stack)
                    .`sType$Default`()
                    .descriptorPool(descriptorPool)
                    .pSetLayouts(stack.longs(descriptorSetLayout))
            val descriptorSet = stack.mallocLong(1)
            checkVk(
                vkAllocateDescriptorSets(device, allocateInfo, descriptorSet),
                "failed to allocate texture descriptor",
            )
            val imageInfo =
                VkDescriptorImageInfo.calloc(1, stack)
                    .sampler(sampler)
                    .imageView(imageView)
                    .imageLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
            val write =
                VkWriteDescriptorSet.calloc(1, stack)
                    .`sType$Default`()
                    .dstSet(descriptorSet.get(0))
                    .dstBinding(0)
                    .descriptorCount(1)
                    .descriptorType(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER)
                    .pImageInfo(imageInfo)
            vkUpdateDescriptorSets(device, write, null)
            descriptorSet.get(0)
        }

    private fun createBuffer(size: Long, usage: Int, properties: Int): BufferAllocation =
        stackPush().use { stack ->
            val createInfo =
                VkBufferCreateInfo.calloc(stack)
                    .`sType$Default`()
                    .size(size)
                    .usage(usage)
                    .sharingMode(VK_SHARING_MODE_EXCLUSIVE)
            val bufferHandle = stack.mallocLong(1)
            checkVk(
                vkCreateBuffer(device, createInfo, null, bufferHandle),
                "failed to create buffer",
            )
            val buffer = bufferHandle.get(0)
            val memoryRequirements = VkMemoryRequirements.malloc(stack)
            vkGetBufferMemoryRequirements(device, buffer, memoryRequirements)
            val allocateInfo =
                VkMemoryAllocateInfo.calloc(stack)
                    .`sType$Default`()
                    .allocationSize(memoryRequirements.size())
                    .memoryTypeIndex(
                        findMemoryType(memoryRequirements.memoryTypeBits(), properties)
                    )
            val memoryHandle = stack.mallocLong(1)
            checkVk(
                vkAllocateMemory(device, allocateInfo, null, memoryHandle),
                "failed to allocate buffer memory",
            )
            val memory = memoryHandle.get(0)
            checkVk(vkBindBufferMemory(device, buffer, memory, 0), "failed to bind buffer memory")
            BufferAllocation(buffer, memory, size)
        }

    private fun destroyBuffer(allocation: BufferAllocation) {
        vkDestroyBuffer(device, allocation.buffer, null)
        vkFreeMemory(device, allocation.memory, null)
    }

    private fun destroyTexture(texture: TextureResource) {
        vkDestroyImageView(device, texture.view, null)
        vkDestroyImage(device, texture.image, null)
        vkFreeMemory(device, texture.memory, null)
    }

    private fun requireWhiteTexture(): TextureResource =
        whiteTexture ?: error("white texture is not initialized")

    private fun recreateSwapchain() {
        vkDeviceWaitIdle(device)
        destroySwapchainObjects()
        if (graphicsPipeline != VK_NULL_HANDLE) {
            vkDestroyPipeline(device, graphicsPipeline, null)
            graphicsPipeline = VK_NULL_HANDLE
        }
        if (renderPass != VK_NULL_HANDLE) {
            vkDestroyRenderPass(device, renderPass, null)
            renderPass = VK_NULL_HANDLE
        }
        createSwapchain()
        createRenderPass()
        createFramebuffers()
        createGraphicsPipeline()
    }

    private fun createFramebuffers() {
        framebuffers =
            swapchainImageViews.mapTo(mutableListOf()) { view ->
                stackPush().use { stack -> createFramebuffer(stack, view) }
            }
    }

    private fun destroySwapchainObjects() {
        framebuffers.forEach { framebuffer -> vkDestroyFramebuffer(device, framebuffer, null) }
        framebuffers.clear()
        swapchainImageViews.forEach { view -> vkDestroyImageView(device, view, null) }
        swapchainImageViews.clear()
        renderFinishedSemaphores.forEach { semaphore ->
            vkDestroySemaphore(device, semaphore, null)
        }
        renderFinishedSemaphores.clear()
        if (swapchain != VK_NULL_HANDLE) {
            vkDestroySwapchainKHR(device, swapchain, null)
            swapchain = VK_NULL_HANDLE
        }
        swapchainImages = emptyList()
    }

    private fun updateViewport() {
        val scale =
            min(
                swapchainExtentWidth.toFloat() / sceneWidth,
                swapchainExtentHeight.toFloat() / sceneHeight,
            )
        viewportWidth = (sceneWidth * scale).toInt().coerceAtLeast(1)
        viewportHeight = (sceneHeight * scale).toInt().coerceAtLeast(1)
        viewportX = (swapchainExtentWidth - viewportWidth) / 2
        viewportY = (swapchainExtentHeight - viewportHeight) / 2
    }

    private fun pointerPosition(): Vec2? {
        if (viewportWidth <= 0 || viewportHeight <= 0) return null
        stackPush().use { stack ->
            val cursorX = stack.mallocDouble(1)
            val cursorY = stack.mallocDouble(1)
            glfwGetCursorPos(window, cursorX, cursorY)
            val windowWidth = stack.mallocInt(1)
            val windowHeight = stack.mallocInt(1)
            glfwGetWindowSize(window, windowWidth, windowHeight)
            val scaleX = swapchainExtentWidth.toDouble() / windowWidth.get(0).coerceAtLeast(1)
            val scaleY = swapchainExtentHeight.toDouble() / windowHeight.get(0).coerceAtLeast(1)
            val framebufferX = cursorX.get(0) * scaleX
            val framebufferY = cursorY.get(0) * scaleY
            val sceneX = ((framebufferX - viewportX) / viewportWidth) * sceneWidth
            val sceneY = ((framebufferY - viewportY) / viewportHeight) * sceneHeight
            return Vec2(sceneX.toFloat(), sceneY.toFloat())
        }
    }

    private fun querySwapchainSupport(device: VkPhysicalDevice): SwapchainSupport =
        stackPush().use { stack ->
            val capabilities = VkSurfaceCapabilitiesKHR.malloc(stack)
            checkVk(
                vkGetPhysicalDeviceSurfaceCapabilitiesKHR(device, surface, capabilities),
                "failed to read surface capabilities",
            )
            val formatCount = stack.mallocInt(1)
            checkVk(
                vkGetPhysicalDeviceSurfaceFormatsKHR(device, surface, formatCount, null),
                "failed to count surface formats",
            )
            val formats = VkSurfaceFormatKHR.malloc(formatCount.get(0), stack)
            checkVk(
                vkGetPhysicalDeviceSurfaceFormatsKHR(device, surface, formatCount, formats),
                "failed to read surface formats",
            )
            val presentModeCount = stack.mallocInt(1)
            checkVk(
                vkGetPhysicalDeviceSurfacePresentModesKHR(
                    device,
                    surface,
                    presentModeCount,
                    null,
                ),
                "failed to count present modes",
            )
            val presentModes = stack.mallocInt(presentModeCount.get(0))
            checkVk(
                vkGetPhysicalDeviceSurfacePresentModesKHR(
                    device,
                    surface,
                    presentModeCount,
                    presentModes,
                ),
                "failed to read present modes",
            )
            SwapchainSupport(
                minImageCount = capabilities.minImageCount(),
                maxImageCount = capabilities.maxImageCount(),
                currentExtentWidth = capabilities.currentExtent().width(),
                currentExtentHeight = capabilities.currentExtent().height(),
                minExtentWidth = capabilities.minImageExtent().width(),
                minExtentHeight = capabilities.minImageExtent().height(),
                maxExtentWidth = capabilities.maxImageExtent().width(),
                maxExtentHeight = capabilities.maxImageExtent().height(),
                supportedUsageFlags = capabilities.supportedUsageFlags(),
                supportedCompositeAlpha = capabilities.supportedCompositeAlpha(),
                currentTransform = capabilities.currentTransform(),
                supportedTransforms = capabilities.supportedTransforms(),
                formats =
                    List(formatCount.get(0)) { index ->
                        SurfaceFormat(
                            format = formats[index].format(),
                            colorSpace = formats[index].colorSpace(),
                        )
                    },
                presentModes = List(presentModeCount.get(0)) { index -> presentModes.get(index) },
            )
        }

    private fun chooseSurfaceFormat(formats: List<SurfaceFormat>): SurfaceFormat {
        check(formats.isNotEmpty()) { "surface reports no formats" }
        val preferred =
            formats.firstOrNull {
                it.format == VK_FORMAT_B8G8R8A8_SRGB &&
                    it.colorSpace == VK_COLOR_SPACE_SRGB_NONLINEAR_KHR
            }
                ?: formats.firstOrNull {
                    it.format == VK_FORMAT_R8G8B8A8_SRGB &&
                        it.colorSpace == VK_COLOR_SPACE_SRGB_NONLINEAR_KHR
                }
                ?: formats.firstOrNull { it.format == VK_FORMAT_B8G8R8A8_UNORM }
                ?: formats.firstOrNull { it.format == VK_FORMAT_R8G8B8A8_UNORM }
        return preferred ?: formats.first()
    }

    private fun choosePresentMode(modes: List<Int>): Int {
        if (!config.vsync && VK_PRESENT_MODE_MAILBOX_KHR in modes) {
            return VK_PRESENT_MODE_MAILBOX_KHR
        }
        return VK_PRESENT_MODE_FIFO_KHR
    }

    private fun chooseSurfaceTransform(support: SwapchainSupport): Int =
        if (support.supportedTransforms and VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR != 0) {
            VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR
        } else {
            support.currentTransform
        }

    private fun chooseCompositeAlpha(capabilities: SwapchainSupport): Int =
        when {
            capabilities.supportedCompositeAlpha and VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR != 0 ->
                VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR
            capabilities.supportedCompositeAlpha and VK_COMPOSITE_ALPHA_INHERIT_BIT_KHR != 0 ->
                VK_COMPOSITE_ALPHA_INHERIT_BIT_KHR
            else -> VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR
        }

    private fun chooseExtent(
        capabilities: SwapchainSupport,
        stack: MemoryStack,
    ): org.lwjgl.vulkan.VkExtent2D {
        val extent = org.lwjgl.vulkan.VkExtent2D.malloc(stack)
        if (capabilities.currentExtentWidth != -1) {
            extent.set(
                capabilities.currentExtentWidth,
                capabilities.currentExtentHeight,
            )
        } else {
            val width = stack.mallocInt(1)
            val height = stack.mallocInt(1)
            glfwGetFramebufferSize(window, width, height)
            extent.set(
                width
                    .get(0)
                    .coerceIn(
                        capabilities.minExtentWidth,
                        capabilities.maxExtentWidth,
                    ),
                height
                    .get(0)
                    .coerceIn(
                        capabilities.minExtentHeight,
                        capabilities.maxExtentHeight,
                    ),
            )
        }
        return extent
    }

    private fun findQueueFamilies(device: VkPhysicalDevice): QueueFamilies =
        stackPush().use { stack ->
            val count = stack.mallocInt(1)
            vkGetPhysicalDeviceQueueFamilyProperties(device, count, null)
            val families = VkQueueFamilyProperties.malloc(count.get(0), stack)
            vkGetPhysicalDeviceQueueFamilyProperties(device, count, families)
            var graphics = -1
            var present = -1
            for (index in 0 until count.get(0)) {
                val supportsGraphics = families[index].queueFlags() and VK_QUEUE_GRAPHICS_BIT != 0
                if (supportsGraphics && graphics < 0) graphics = index
                val supportsPresent = stack.mallocInt(1)
                vkGetPhysicalDeviceSurfaceSupportKHR(device, index, surface, supportsPresent)
                if (supportsPresent.get(0) == 1 && present < 0) present = index
                if (graphics >= 0 && present >= 0) break
            }
            check(graphics >= 0 && present >= 0) { "device has no graphics and present queues" }
            QueueFamilies(graphics, present)
        }

    private fun isDeviceSuitable(device: VkPhysicalDevice): Boolean {
        if (VK_KHR_SWAPCHAIN_EXTENSION_NAME !in enumerateDeviceExtensions(device)) {
            return false
        }
        val families = runCatching { findQueueFamilies(device) }.getOrNull() ?: return false
        val support = runCatching { querySwapchainSupport(device) }.getOrNull() ?: return false
        return families.graphics >= 0 &&
            families.present >= 0 &&
            support.formats.isNotEmpty() &&
            support.presentModes.isNotEmpty()
    }

    private fun enumerateInstanceExtensions(): Set<String> =
        stackPush().use { stack ->
            val count = stack.mallocInt(1)
            checkVk(
                vkEnumerateInstanceExtensionProperties(null as CharSequence?, count, null),
                "failed to enumerate instance extensions",
            )
            VkExtensionProperties.malloc(count.get(0)).use { properties ->
                checkVk(
                    vkEnumerateInstanceExtensionProperties(
                        null as CharSequence?,
                        count,
                        properties,
                    ),
                    "failed to read instance extensions",
                )
                (0 until count.get(0)).mapTo(mutableSetOf()) { index ->
                    properties[index].extensionNameString()
                }
            }
        }

    private fun enumerateDeviceExtensions(device: VkPhysicalDevice): Set<String> =
        stackPush().use { stack ->
            val count = stack.mallocInt(1)
            checkVk(
                vkEnumerateDeviceExtensionProperties(
                    device,
                    null as CharSequence?,
                    count,
                    null,
                ),
                "failed to enumerate device extensions",
            )
            VkExtensionProperties.malloc(count.get(0)).use { properties ->
                checkVk(
                    vkEnumerateDeviceExtensionProperties(
                        device,
                        null as CharSequence?,
                        count,
                        properties,
                    ),
                    "failed to read device extensions",
                )
                (0 until count.get(0)).mapTo(mutableSetOf()) { index ->
                    properties[index].extensionNameString()
                }
            }
        }

    private fun getQueue(device: VkDevice, family: Int): VkQueue =
        stackPush().use { stack ->
            val queueHandle = stack.mallocPointer(1)
            vkGetDeviceQueue(device, family, 0, queueHandle)
            VkQueue(queueHandle.get(0), device)
        }

    private fun findMemoryType(typeFilter: Int, properties: Int): Int =
        stackPush().use { stack ->
            val memoryProperties = VkPhysicalDeviceMemoryProperties.malloc(stack)
            vkGetPhysicalDeviceMemoryProperties(physicalDevice, memoryProperties)
            for (index in 0 until memoryProperties.memoryTypeCount()) {
                if (
                    typeFilter and (1 shl index) != 0 &&
                        memoryProperties.memoryTypes(index).propertyFlags() and properties ==
                            properties
                ) {
                    return index
                }
            }
            error("no compatible vulkan memory type")
        }

    private fun createImageView(
        device: VkDevice,
        image: Long,
        format: Int,
    ): Long =
        stackPush().use { stack ->
            val viewInfo =
                VkImageViewCreateInfo.calloc(stack)
                    .`sType$Default`()
                    .image(image)
                    .viewType(VK_IMAGE_VIEW_TYPE_2D)
                    .format(format)
                    .components { components ->
                        components.r(VK_COMPONENT_SWIZZLE_IDENTITY)
                        components.g(VK_COMPONENT_SWIZZLE_IDENTITY)
                        components.b(VK_COMPONENT_SWIZZLE_IDENTITY)
                        components.a(VK_COMPONENT_SWIZZLE_IDENTITY)
                    }
                    .subresourceRange { range ->
                        range.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                        range.baseMipLevel(0)
                        range.levelCount(1)
                        range.baseArrayLayer(0)
                        range.layerCount(1)
                    }
            val view = stack.mallocLong(1)
            checkVk(
                vkCreateImageView(device, viewInfo, null, view),
                "failed to create image view",
            )
            view.get(0)
        }

    private fun createFramebuffer(stack: MemoryStack, view: Long): Long {
        val createInfo =
            VkFramebufferCreateInfo.calloc(stack)
                .`sType$Default`()
                .renderPass(renderPass)
                .attachmentCount(1)
                .pAttachments(stack.longs(view))
                .width(swapchainExtentWidth)
                .height(swapchainExtentHeight)
                .layers(1)
        val framebuffer = stack.mallocLong(1)
        checkVk(
            vkCreateFramebuffer(device, createInfo, null, framebuffer),
            "failed to create framebuffer",
        )
        return framebuffer.get(0)
    }

    private fun createShaderModule(device: VkDevice, code: ByteBuffer): Long =
        stackPush().use { stack ->
            val createInfo = VkShaderModuleCreateInfo.calloc(stack).`sType$Default`().pCode(code)
            val module = stack.mallocLong(1)
            checkVk(
                vkCreateShaderModule(device, createInfo, null, module),
                "failed to create shader module",
            )
            module.get(0)
        }

    private fun createSemaphore(): Long =
        stackPush().use { stack ->
            val createInfo = VkSemaphoreCreateInfo.calloc(stack).`sType$Default`()
            val semaphore = stack.mallocLong(1)
            checkVk(
                vkCreateSemaphore(device, createInfo, null, semaphore),
                "failed to create render semaphore",
            )
            semaphore.get(0)
        }

    private fun loadShader(name: String): ByteBuffer {
        val bytes =
            javaClass.getResourceAsStream("/shaders/$name")?.use { stream -> stream.readBytes() }
                ?: error("shader '$name' is missing")
        val buffer = memAlloc(bytes.size)
        buffer.put(bytes).flip()
        return buffer
    }

    private fun rgbaWhitePixel(): ByteBuffer =
        ByteBuffer.allocateDirect(4)
            .put(0xff.toByte())
            .put(0xff.toByte())
            .put(0xff.toByte())
            .put(0xff.toByte())
            .apply { flip() }

    private fun fallbackColor(key: AssetKey): Color {
        val hash = key.value.hashCode()
        val hue = ((hash ushr 8) and 0xff) / 255f
        return Color(
            red = 0.08f + hue * 0.2f,
            green = 0.1f + (1f - hue) * 0.14f,
            blue = 0.18f + hue * 0.25f,
        )
    }

    private fun checkVk(condition: Boolean, message: String) {
        check(condition) { message }
    }

    private fun checkVk(result: Int, message: String) {
        if (result != VK_SUCCESS) {
            error("$message (vkresult=$result)")
        }
    }

    private fun isMac(): Boolean = System.getProperty("os.name").lowercase().contains("mac")

    private data class Uv(
        val u0: Float,
        val v0: Float,
        val u1: Float,
        val v1: Float,
    )

    private companion object {
        const val VERTEX_FLOAT_COUNT: Int = 8
        const val VERTEX_BUFFER_START_SIZE: Long = 64L * 1024L
        const val VK_ERROR_OUT_OF_DATE_KHR: Int = -1000001004
        const val VK_SUBOPTIMAL_KHR: Int = 1000001003
        const val VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT: Int = 0x00000010
        const val VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL: Int = 2
        const val VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT: Int = 0x00000001
        const val VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT: Int = 0x00002000
        const val VK_SUBPASS_CONTENTS_INLINE: Int = 0
        const val VK_SAMPLER_MIPMAP_MODE_LINEAR: Int = 1
        const val VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE: Int = 2
        const val VK_FORMAT_R32G32_SFLOAT: Int = 103
        const val VK_FORMAT_R32G32B32A32_SFLOAT: Int = 109
        const val VK_FORMAT_FEATURE_SAMPLED_IMAGE_BIT: Int = 0x00000001
        const val VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU: Int = 2
        const val VK_PHYSICAL_DEVICE_TYPE_INTEGRATED_GPU: Int = 1
    }
}
