package net.codjo.test.release.util.comparisonstrategy;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDPage;
import org.pdfbox.pdmodel.common.PDRectangle;
import org.pdfbox.pdmodel.font.PDFont;
import org.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.pdfbox.util.PDFTextStripper;

public class PDFComparisonStrategy implements ComparisonStrategy {

    private final File expectedFile;
    private final File actualFile;


    public PDFComparisonStrategy(File expectedFile, File actualFile) {
        this.expectedFile = expectedFile;
        this.actualFile = actualFile;
    }


    public boolean compare() throws Exception {

        PDDocument expectedDoc = PDDocument.load(expectedFile);
        PDDocument actualDoc = PDDocument.load(actualFile);

        if (!(expectedDoc.getNumberOfPages() == actualDoc.getNumberOfPages())) {
            return false;
        }

        boolean comparaison = compareText();

        try {

            List allActualsPages = actualDoc.getDocumentCatalog().getAllPages();
            List allExpectedPages = expectedDoc.getDocumentCatalog().getAllPages();
            PDFPageComparisonStrategy pageComparator = new PDFPageComparisonStrategy();

            for (int i = 0; i < allExpectedPages.size(); i++) {

                pageComparator.setActualPage((PDPage)allActualsPages.get(i));
                pageComparator.setExpectedPage((PDPage)allExpectedPages.get(i));
                comparaison = comparaison && (pageComparator.compare());
            }
        }
        finally {
            close(expectedDoc);
            close(actualDoc);
        }

        return comparaison;
    }


    private boolean compareText() throws IOException {

        PDFTextStripper stripper = new PDFTextStripper();
        PDDocument actualDoc = null;
        PDDocument expectedDoc = null;

        boolean status = false;

        try {
            actualDoc = PDDocument.load(actualFile);
            expectedDoc = PDDocument.load(expectedFile);
            status = stripper.getText(actualDoc).equals(stripper.getText(expectedDoc));
        }
        finally {
            close(actualDoc);
            close(expectedDoc);
        }

        return status;
    }


    private void close(PDDocument actualDoc) throws IOException {
        if (actualDoc != null) {
            actualDoc.close();
        }
    }


    private class PDFPageComparisonStrategy implements ComparisonStrategy {

        PDPage expectedPage;
        PDPage actualPage;


        private PDFPageComparisonStrategy() {
        }


        public boolean compare() throws Exception {
            return compareFonts() && compareImages();
        }


        private boolean compareFonts() throws IOException {

            Map actualFonts = actualPage.getResources().getFonts();
            Map expectedFonts = expectedPage.getResources().getFonts();

            if (expectedFonts.size() != actualFonts.size()) {
                return false;
            }

            boolean equals = true;
            for (Object key : expectedFonts.keySet()) {
                equals = equals && compareFont((PDFont)actualFonts.get(key), (PDFont)expectedFonts.get(key));
            }
            return equals;
        }


        private boolean compareFont(PDFont actualFont, PDFont expectedFont) throws IOException {

            PDRectangle actualRect = actualFont.getFontBoundingBox();
            PDRectangle expectedRect = expectedFont.getFontBoundingBox();

            return ((actualFont.getBaseFont().equals(expectedFont.getBaseFont())) &&
                    (actualRect.getHeight() == expectedRect.getHeight()) &&
                    (actualRect.getWidth() == expectedRect.getWidth()) &&
                    (actualRect.getLowerLeftX() == expectedRect.getLowerLeftX()) &&
                    (actualRect.getLowerLeftY() == expectedRect.getLowerLeftY()));
        }


        private boolean compareImages() throws IOException {

            Map actualImages = actualPage.getResources().getImages();
            Map expectedImages = expectedPage.getResources().getImages();

            if (actualImages.size() != expectedImages.size()) {
                return false;
            }

            boolean equals = true;
            for (Object key : expectedImages.keySet()) {
                PDXObjectImage actualImage = (PDXObjectImage)actualImages.get(key);
                PDXObjectImage expectedImage = (PDXObjectImage)expectedImages.get(key);
                equals = equals && compareImage(expectedImage, actualImage);
            }

            return equals;
        }


        private boolean compareImage(PDXObjectImage expectedImage, PDXObjectImage actualImage) {

            boolean equals = (expectedImage.getHeight() == actualImage.getHeight());
            equals = equals && (expectedImage.getWidth() == actualImage.getWidth());
            return equals;
        }


        public PDPage getExpectedPage() {
            return expectedPage;
        }


        public PDPage getActualPage() {
            return actualPage;
        }


        public void setExpectedPage(PDPage expectedPage) {
            this.expectedPage = expectedPage;
        }


        public void setActualPage(PDPage actualPage) {
            this.actualPage = actualPage;
        }
    }
}
