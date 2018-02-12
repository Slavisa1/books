package com.misic.books.seervice;

import com.misic.books.BookBuilder;
import com.misic.books.PageBuilder;
import com.misic.books.controller.BookRestController;
import com.misic.books.entity.Book;
import com.misic.books.service.BookService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.*;

public class BookServiceTest {
    private final List<Book> books = new ArrayList<Book>();

    @Before
    public void initBooks() {
        Book book = new Book();
        book.setId(1L);
        book.setAuthor("Филиппов С. А.");
        book.setTitle("Уроки робототехники. Конструкция. Движение. Управление");
        book.setDescription("Учебное пособие знакомит с основами моделирования автоматических устройств на основе робототехнических конструкторов LEGO и TRIK и создания алгоритмов управления роботами в среде TRIK Studio.");
        book.setIsbn("978-5-00101-114-9");
        book.setPrintYear(2018);
        book.setReadAlready(false);

        books.add(book);
    }

    @Test
    public void
    getAllBookTest() {
        BookService bookService = mock(BookService.class);
        when(bookService.findAll()).thenReturn(books);

        BookRestController bookRestController = new BookRestController();

        ReflectionTestUtils.setField(bookRestController, "bookService", bookService);

        ExtendedModelMap uiModel = new ExtendedModelMap();
        uiModel.addAttribute("books", bookRestController.getAllBook());

        assertEquals(1, books.size());
    }

    @Test
    public void createTest() {
        final Book newBook = new Book();
        newBook.setId(999L);
        newBook.setAuthor("Васин Н. Н.");
        newBook.setTitle("Основы сетевых технологий на базе коммутаторов и маршрутизаторов : учебное пособие");
        newBook.setDescription("В курсе лекций излагаются основы построения сетей. Рассматриваются семиуровневая модель и модель TCP/IP, прикладной и транспортный уровень, физический уровень модели.");
        newBook.setIsbn("978-5-9963-0489-9");
        newBook.setPrintYear(2011);
        newBook.setReadAlready(false);

        BookService bookService = mock(BookService.class);
        when(bookService.save(newBook)).thenAnswer((Answer<Book>) invocationOnMock -> {
            books.add(newBook);
            return newBook;
        });

        BookRestController bookRestController = new BookRestController();
        ReflectionTestUtils.setField(bookRestController, "bookService", bookService);

        Book book = bookRestController.create(newBook);
        assertEquals(999L, book.getId());
        assertEquals("Васин Н. Н.", book.getAuthor());
        assertEquals("978-5-9963-0489-9", book.getIsbn());

        assertEquals(2, books.size());
    }

    @Test
    public void getPageBooksTest() throws Exception {
        Sort sort = new Sort(Sort.Direction.ASC, "id");

        Page<Book> bookPage = new PageBuilder<Book>()
                .elements(books)
                .pageRequest(new PageRequest(0, 10, sort))
                .totalElements(1)
                .build();

        BookService bookService = mock(BookService.class);

        when(bookService.findAllByPage(isA(Pageable.class))).thenReturn(bookPage);

        BookRestController bookRestController = new BookRestController();
        ReflectionTestUtils.setField(bookRestController, "bookService", bookService);

        Page<Book> books = bookRestController.getPageBooks(1, "id", "asc");

        verify(bookService).findAllByPage(notNull(Pageable.class));

        assertEquals(1, books.getTotalElements());
        assertEquals(1, books.getTotalPages());
    }

    @Test
    public void findBookByIdTest() throws Exception {
        BookService bookService = mock(BookService.class);
        when(bookService.findById(1L)).thenAnswer((Answer<Book>) invocationOnMock -> {
            for (Book book : books) {
                if (book.getId() == 1L) {
                    return book;
                }
            }
            return null;
        });

        BookRestController bookRestController = new BookRestController();
        ReflectionTestUtils.setField(bookRestController, "bookService", bookService);

        Book book = bookRestController.findBookById(1L);

        assertEquals(1, book.getId());
    }

    @Test
    public void deleteTest() throws Exception {
        BookService bookService = mock(BookService.class);

        doAnswer((Answer<Void>) invocationOnMock -> {
            books.remove(books.get(0));
            return null;
        }).when(bookService).delete(any(Book.class));

        BookRestController bookRestController = new BookRestController();
        ReflectionTestUtils.setField(bookRestController, "bookService", bookService);

        bookRestController.delete(1L);

        assertEquals(0, books.size());
    }

    @Test
    public void updateTest() throws Exception{
        final Book updateDataBook = new BookBuilder().readAlready(true).build();

        BookService bookService = mock(BookService.class);
        when(bookService.update(updateDataBook, 1L)).thenAnswer((Answer<Book>) invocationOnMock ->{
           Book book = books.get(0);
           if (updateDataBook.getAuthor() != null){
               book.setAuthor(book.getAuthor());
           }

           if (updateDataBook.getTitle() != null){
               book.setTitle((book.getTitle()));
           }

            if (updateDataBook.getDescription() != null){
                book.setDescription((book.getDescription()));
            }

            if (updateDataBook.getIsbn() != null){
                book.setIsbn((book.getIsbn()));
            }

            if (updateDataBook.getPrintYear() != 0){
                book.setPrintYear((book.getPrintYear()));
            }

            book.setReadAlready(true);
           return book;
        });

        BookRestController bookRestController = new BookRestController();
         ReflectionTestUtils.setField(bookRestController, "bookService", bookService);

         Book book = bookRestController.update(updateDataBook, 1L);
         assertEquals(1L, book.getId());
         assertEquals(true, book.isReadAlready());

         assertEquals(1, books.size());
    }
}

