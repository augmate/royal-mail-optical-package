#include <stdio.h>
#include "Helpers.h"

#include <zxing/common/Counted.h>
#include <zxing/Binarizer.h>
#include <zxing/MultiFormatReader.h>
#include <zxing/Result.h>
#include <zxing/ReaderException.h>
#include <zxing/common/GlobalHistogramBinarizer.h>
#include <zxing/common/HybridBinarizer.h>
#include <zxing/common/GreyscaleLuminanceSource.h>
#include <exception>
#include <zxing/Exception.h>
#include <zxing/common/IllegalArgumentException.h>
#include <zxing/BinaryBitmap.h>
#include <zxing/DecodeHints.h>

#include <zxing/qrcode/QRCodeReader.h>
#include <zxing/multi/qrcode/QRCodeMultiReader.h>
#include <zxing/multi/ByQuadrantReader.h>
#include <zxing/multi/MultipleBarcodeReader.h>
#include <zxing/multi/GenericMultipleBarcodeReader.h>

#include <zxing/qrcode/detector/Detector.h>

using namespace std;
using namespace zxing;
using namespace zxing::multi;
using namespace zxing::qrcode;

namespace {
    bool more = false;
    bool test_mode = false;
    bool try_harder = false;
    bool search_multi = false;
    bool use_hybrid = false;
    bool use_global = false;
    bool verbose = false;
}

vector<Ref<Result> > decode(Ref<BinaryBitmap> image, DecodeHints hints) {
  Ref<Reader> reader(new MultiFormatReader);
  return vector<Ref<Result> >(1, reader->decode(image, hints));
}

vector<Ref<Result> > decode_multi(Ref<BinaryBitmap> image, DecodeHints hints) {
  MultiFormatReader delegate;
  GenericMultipleBarcodeReader reader(delegate);
  return reader.decodeMultiple(image, hints);
}

int read_image(Ref<LuminanceSource> source, bool hybrid) {
    //vector<Ref<Result> > results;
    Ref<Result> result;
    int res = -1;

    // for some reason throwing and handling an exception here
    // keeps zxing-lib from crashing and bringing down the entire application.
    // zxing internally uses exceptions for control-flow and returning results.
    // TODO: figure out why the hell this works. it's prolly an exciting reason.

    try {
        LOGD("Practicing throwing a ReaderException..\n");
        throw zxing::Exception("practice");
    }
    catch(std::exception& err) {
        LOGD("Caught practice throw. Moving onto better things..\n");
    }

    // TODO ton of room for optimization here! :)

    Ref<Binarizer> binarizer;
    if (hybrid) {
      binarizer = new HybridBinarizer(source);
    } else {
      binarizer = new GlobalHistogramBinarizer(source);
    }
    DecodeHints hints(DecodeHints::QR_CODE_HINT);
    //hints.setTryHarder(try_harder);

    try {
        LOGD("setting up binary bitmap reference..\n");
        Ref<BinaryBitmap> binary(new BinaryBitmap(binarizer));
        LOGD("Spawning native qrcode decoder..\n");
        Ref<zxing::Reader> qrDecoder(new zxing::qrcode::QRCodeReader());
        LOGD("Decodering..\n");
        result = qrDecoder->decode(binary, hints);
        LOGD("Decodering.. Success\n");

        res = 0;
    }
    catch(ReaderException& e) {
        LOGD("Caught ReaderException from decoder.\n");
    }
    catch(std::exception& e) {
        LOGD("Caught a generic exception from decoder.\n");
    }

    if(!res) {
        LOGD("Binarizer succeeded\n");
        LOGD("Format: %s\n", BarcodeFormat::barcodeFormatNames[result->getBarcodeFormat()]);
        LOGD("Result value: %s\n", result->getText()->getText().c_str());
    }

/*
  try {
    Ref<Binarizer> binarizer;
    if (hybrid) {
      binarizer = new HybridBinarizer(source);
    } else {
      binarizer = new GlobalHistogramBinarizer(source);
    }
    DecodeHints hints(DecodeHints::DEFAULT_HINT);
    hints.setTryHarder(try_harder);
    Ref<BinaryBitmap> binary(new BinaryBitmap(binarizer));
    if (search_multi) {
      results = decode_multi(binary, hints);
    } else {
      results = decode(binary, hints);
    }
    res = 0;
  } catch (const ReaderException& e) {
    cell_result = "zxing::ReaderException: " + string(e.what());
    res = -2;
  } catch (const zxing::IllegalArgumentException& e) {
    cell_result = "zxing::IllegalArgumentException: " + string(e.what());
    res = -3;
  } catch (const zxing::Exception& e) {
    cell_result = "zxing::Exception: " + string(e.what());
    res = -4;
  } catch (const std::exception& e) {
    cell_result = "std::exception: " + string(e.what());
    res = -5;
  }
  */

/*
  if (res != 0 && (verbose || (use_global ^ use_hybrid))) {
    cout << (hybrid ? "Hybrid" : "Global")
         << " binarizer failed: " << cell_result << endl;
  } else if (!test_mode) {
    if (verbose) {
      cout << (hybrid ? "Hybrid" : "Global")
           << " binarizer succeeded: " << endl;
    }
    for (size_t i = 0; i < results.size(); i++) {
      if (more) {
        cout << "  Format: "
             << BarcodeFormat::barcodeFormatNames[results[i]->getBarcodeFormat()]
             << endl;
        for (int j = 0; j < results[i]->getResultPoints()->size(); j++) {
          cout << "  Point[" << j <<  "]: "
               << results[i]->getResultPoints()[j]->getX() << " "
               << results[i]->getResultPoints()[j]->getY() << endl;
        }
      }
      if (verbose) {
        cout << "    ";
      }
      cout << results[i]->getText()->getText() << endl;
    }
  }
  */

  return res;
}

extern "C" {
    void zxingNativeDecode(unsigned char* src, unsigned int width, unsigned int height) {
        LOGD("Executing native decoder under: %s\n", ABI);

        zxing::ArrayRef<char> image = zxing::ArrayRef<char>((char*) src, width * height);
        zxing::GreyscaleLuminanceSource* luminance = new zxing::GreyscaleLuminanceSource(image, width, height, 0, 0, width, height);
        zxing::Ref<LuminanceSource> luminanceSource = zxing::Ref<LuminanceSource>(luminance);
        read_image(luminanceSource, true);

        LOGD("Executed native zxing!\n");
    }

    int main() {
        printf("Giving it a try from CLI..\n");

        int width = 640;
        int height = 360;
        char* src = new char[width * height];

        zxing::ArrayRef<char> image = zxing::ArrayRef<char>((char*) src, width * height);
        zxing::GreyscaleLuminanceSource* luminance = new zxing::GreyscaleLuminanceSource(image, width, height, 0, 0, width, height);
        zxing::Ref<LuminanceSource> luminanceSource = zxing::Ref<LuminanceSource>(luminance);
        read_image(luminanceSource, false);

        LOGD("Executed native zxing!\n");

        return 0;
    }
}