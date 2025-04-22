import java.util.*;

// =============================
// Exception Classes
// =============================
class BookNotAvailableException extends Exception {
    public BookNotAvailableException(String message) {
        super(message);
    }
}

class BookNotFoundException extends Exception {
    public BookNotFoundException(String message) {
        super(message);
    }
}

// =============================
// Book Class
// =============================
class Book {
    private String title;
    private String author;
    private String genre;
    private boolean isAvailable;

    public Book(String title, String author, String genre) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.isAvailable = true;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void checkout() throws BookNotAvailableException {
        if (!isAvailable) {
            throw new BookNotAvailableException("This book is already checked out.");
        }
        isAvailable = false;
    }

    public void returnBook() {
        isAvailable = true;
    }

    public String getGenre() {
        return genre;
    }

    public String getTitle() {
        return title;
    }

    public String toString() {
        return title + " by " + author + " (" + genre + ") " + (isAvailable ? "[Available]" : "[Checked Out]");
    }
}

// =============================
// LibraryCollection Class
// =============================
class LibraryCollection {
    private Map<String, List<Book>> genreMap;

    public LibraryCollection() {
        genreMap = new HashMap<>();
    }

    public void addBook(Book book) {
        genreMap.computeIfAbsent(book.getGenre(), k -> new ArrayList<>()).add(book);
    }

    public Iterator<Book> getGenreIterator(String genre) {
        List<Book> books = genreMap.getOrDefault(genre, new ArrayList<>());
        List<Book> availableBooks = new ArrayList<>();
        for (Book book : books) {
            if (book.isAvailable()) {
                availableBooks.add(book);
            }
        }
        return availableBooks.iterator();
    }

    public Book findBook(String genre, String title) throws BookNotFoundException {
        List<Book> books = genreMap.get(genre);
        if (books != null) {
            for (Book book : books) {
                if (book.getTitle().equalsIgnoreCase(title)) {
                    return book;
                }
            }
        }
        throw new BookNotFoundException("Book titled '" + title + "' not found in genre '" + genre + "'.");
    }
}

// =============================
// Main Program
// =============================
public class LibraryTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LibraryCollection library = new LibraryCollection();

        // Sample Books (✅ 교체된 책 반영)
        library.addBook(new Book("The Hobbit", "J.R.R. Tolkien", "Fantasy"));
        library.addBook(new Book("The Da Vinci Code", "Dan Brown", "Mystery"));
        library.addBook(new Book("Ready Player One", "Ernest Cline", "Science Fiction"));
        library.addBook(new Book("Brave New World", "Aldous Huxley", "Dystopian"));

        while (true) {
            System.out.println("\nChoose a genre to see available books, or type 'exit' to finish:");
            String genre = scanner.nextLine();
            if (genre.equalsIgnoreCase("exit")) break;

            Iterator<Book> iterator = library.getGenreIterator(genre);
            boolean hasBooks = false;
            System.out.println("Available books in " + genre + ":");
            while (iterator.hasNext()) {
                System.out.println("- " + iterator.next());
                hasBooks = true;
            }

            if (!hasBooks) {
                System.out.println("No available books in this genre.");
                continue;
            }

            System.out.print("\nEnter the title of the book you'd like to borrow or return: ");
            String title = scanner.nextLine();

            try {
                Book book = library.findBook(genre, title);
                if (book.isAvailable()) {
                    book.checkout();
                    System.out.println("You checked out: " + book.getTitle());
                } else {
                    book.returnBook();
                    System.out.println("You returned: " + book.getTitle());
                }
            } catch (BookNotFoundException | BookNotAvailableException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
        System.out.println("Library session ended. Have a great day!");
    }
}
