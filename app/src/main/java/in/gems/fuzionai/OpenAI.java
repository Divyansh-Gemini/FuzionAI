package in.gems.fuzionai;

// OpenAI Documentation
// https://platform.openai.com/docs/introduction

// OpenAI API Reference
// https://platform.openai.com/docs/api-reference/

public class OpenAI {

    // OpenAI APIs
    public static final String API_GET_LIST_MODELS = "https://api.openai.com/v1/models";
    public static final String API_GET_RETRIEVE_MODEL = "https://api.openai.com/v1/models/";        // .../{model}
    public static final String API_POST_COMPLETION = "https://api.openai.com/v1/completions";
    public static final String API_POST_CHAT = "https://api.openai.com/v1/chat/completions";
    public static final String API_POST_EDIT = "https://api.openai.com/v1/edits";
    public static final String API_POST_IMAGE = "https://api.openai.com/v1/images/generations";
    public static final String API_POST_IMAGE_EDIT = "https://api.openai.com/v1/images/edits";
    public static final String API_POST_IMAGE_VARIATION = "https://api.openai.com/v1/images/variations";
    public static final String API_POST_EMBEDDINGS = "https://api.openai.com/v1/embeddings";
    public static final String API_POST_TRANSCRIPTION = "https://api.openai.com/v1/audio/transcriptions";
    public static final String API_POST_TRANSLATION = "https://api.openai.com/v1/audio/translations";
    public static final String API_GET_LIST_FILES = "https://api.openai.com/v1/files";
    public static final String API_POST_UPLOAD_FILE = "https://api.openai.com/v1/files";
    public static final String API_DELETE_DELETE_FILE = "https://api.openai.com/v1/files/";         // .../{file_id}
    public static final String API_GET_RETRIEVE_FILE = "https://api.openai.com/v1/files/";          // .../{file_id}
    public static final String API_GET_RETRIEVE_FILE_CONTENT = "https://api.openai.com/v1/files/";  // .../{file_id}/content
    public static final String API_POST_CREATE_FINE_TUNE = "";
    public static final String API_GET_LIST_FINE_TUNES = "";
    public static final String API_GET_RETRIEVE_FINE_TUNE = "";
    public static final String API_POST_CANCEL_FINE_TUNE = "";
    public static final String API_GET_LIST_FINE_TUNE_EVENTS = "";
    public static final String API_DELETE_DELETE_FINE_TUNE_MODEL = "";
    public static final String API_POST_MODERATION = "";

    // OpenAI API Key
    public static final String API_KEY = "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";
}
