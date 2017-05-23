package androlite.baitaplon;

/**
 * Created by Zenphone on 3/21/2017.
 */

public class Note {
    private int noteId;
    private String noteWord;
    private String noteContent;
    public Note()  {

    }
    public Note(  String noteWord) {
        this.setNoteWord(noteWord);

    }
    public Note(  String noteTitle, String noteContent) {
        this.setNoteWord(noteTitle);
        this.setNoteContent(noteContent);
    }
    public Note(int noteId, String noteWord, String noteContent) {
        this.setNoteId(noteId);
        this.setNoteWord(noteWord);
        this.setNoteContent(noteContent);
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public String getNoteWord() {
        return noteWord;
    }

    public void setNoteWord(String noteWord) {
        this.noteWord = noteWord;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }
}
