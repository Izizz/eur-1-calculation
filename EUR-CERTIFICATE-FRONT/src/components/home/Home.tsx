import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./home.css";

const Home: React.FC = () => {
  const navigateTo = useNavigate();

  useEffect(() => {
    if (!window.localStorage.getItem("user")) {
      navigateTo("/login");
    }
  },);

  return (
    <div className="home-container">
      <header className="jumbotron-a ukrainian-instructions">
        <div className="head-text">
          <h2>Як користуватися? (Українська)</h2>
          <ol>
            <li>
              <strong>Завантажити приклади файлів:</strong>
              <p>Цей блок містить дві кнопки для завантаження прикладів файлів. Файли надають приклади потрібного формату для ваших вхідних файлів.</p>
              <ul>
                <li>Кнопка "Example of bill of materials" надає приклад файлу списку матеріалів у форматі <strong>.xls </strong>.</li>
                <li>Кнопка "Example of expenses file" надає приклад файлу витрат у форматі <strong>.xlsx</strong> .</li>
              </ul>
              <p>Щоб завантажити ці файли, просто натисніть на відповідні кнопки. Використовуйте ці файли як шаблони для підготовки ваших власних файлів.</p>
            </li>
            <li>
              <strong>Вхідні файли:</strong>
              <p>Цей блок містить два поля для завантаження файлів.</p>
              <ol>
                <li>
                  <strong>Файл витрат:</strong> Щоб завантажити файл, натисніть кнопку 'Обрати файл' у полі "Expenses file". Це відкриє вікно вибору файлу. Знайдіть і виберіть відповідний файл у системі. Зверніть увагу, що цей файл повинен відповідати формату завантаженого "Example of expenses file".
                  <strong>Файл обов'язково не повинен містити нічого крім таблиці!!</strong>
                </li>
                <li>
                  <strong>Список матеріалів:</strong> Аналогічно, щоб завантажити файл списку матеріалів, натисніть кнопку 'Обрати файл' у полі "Bill of materials". Знову переконайтеся, що цей файл відповідає формату завантаженого "Example of bill of materials".
                  <strong>Файл обов'язково не повинен містити нічого крім таблиці!!</strong>
                
                </li>
              </ol>
            </li>
            <li>
              <strong>Назва продукту:</strong>
              <p>Введіть назву продукту, для якого ви хочете зробити розрахунок, у поле "Product title".</p>
            </li>
            <li>
              <strong>Відправити:</strong>
              <p>Після того, як ви вибрали всі потрібні файли і ввели назву продукту, просто натисніть кнопку 'Submit'. Процес розрахунку почнеться, і ви отримаєте результати після завершення обчислень.</p>
            </li>
            <li>
              <strong>Завантаження готових файлів:</strong>
              <p>Якщо обчислення успішно завершені, ви побачите новий блок із посиланнями на завантаження ваших розрахункових файлів. Просто натисніть на відповідні посилання, щоб завантажити файли.</p>
            </li>
            <li>
              <strong>Приблизний час обчислення:</strong>
              <p>Зверніть увагу, що приблизний час обчислення може коливатися від 2 до 10 хвилин в залежності від поточного навантаження на сервер.</p>
            </li>
          </ol>
        </div>
      </header>
      <header className="jumbotron-a">
        <div className="head-text">
          <h1>Welcome to the calculation service for On Site Company UA</h1>
        </div>
        <div className="head-text">
          <h2>How to use?</h2>
          <ol>
            <li>
              <strong>Download Sample Files:</strong>
              <p>This block contains two buttons to download sample files. The files provide examples of the required format for your input files.</p>
              <ul>
                <li>The "Example of bill of materials" button provides a sample of a bill of materials file in .xls format.</li>
                <li>The "Example of expenses file" button provides a sample of an expenses file in .xlsx format.</li>
              </ul>
              <p>To download these files, simply click on the respective buttons. Use these files as templates for preparing your own files.</p>
            </li>
            <li>
              <strong>Input Files:</strong>
              <p>This block contains two input fields for uploading files.</p>
              <ol>
                <li>
                  <strong>Expenses file:</strong> To upload the file, click on the 'Choose File' button in the "Expenses file" field. This will open a file browser. Locate and select the appropriate file on your system. Please note that this file should match the format of the downloaded "Example of expenses file".
                </li>
                <li>
                  <strong>Bill of materials:</strong> Similarly, to upload the bill of materials file, click on the 'Choose File' button in the "Bill of materials" field. Again, ensure that this file matches the format of the downloaded "Example of bill of materials".
                </li>
              </ol>
            </li>
            <li>
              <strong>Product Name Input Field:</strong>
              <p>The third block is for entering the product title. Enter the name of the product you want to calculate in the "Product title" field. Ensure the product name is entered correctly before proceeding.</p>
            </li>
          </ol>
          <h2>Submitting the Form</h2>
          <p>Once you have uploaded both files and entered the product name, you can submit the form by clicking on the 'Submit' button.</p>
          <p>If the calculation is successful, a success message will be displayed, and the calculated files will be ready for download. In case of an error during the calculation, an error message will be displayed.</p>
          <h2>Downloading the Calculated Files</h2>
          <p>Upon a successful calculation, a set of calculated files will be available for download. Simply click on the 'Download' button corresponding to each file to download them to your system.</p>
          <p>Remember to ensure the files for upload are in the correct format as per the sample files for a smooth calculation process.</p>
          <ol>
          <li>

<strong>Estimated Calculation Time:</strong>
<p>Please note that the estimated time for the calculation process can range from 2 to 10 minutes depending on the current load on the server.</p>
</li> 
          </ol>
          
        </div>
      </header>
  
    </div>
  );
};

export default Home;
